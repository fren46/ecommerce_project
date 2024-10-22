package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.ProductAlreadyExistsException
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.Product
import com.ecomm.commons.UserRole
import com.ecomm.commons.WarningDTO
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.lang.IllegalArgumentException
import java.net.URI

import java.util.*

@Mapper
interface ProductMapper {
    fun toDto(product: Product): ProductDTO
    fun toModel(productDTO: ProductDTO): Product
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val orderServiceImpl: OrderServiceImpl
    ): ProductService {

    @Value("\${application.urlWarehouseService}")
    private lateinit var HostWarehouseS: String
    private val mapper = Mappers.getMapper(ProductMapper::class.java)
    val restTemplate = RestTemplate()

    override fun getProducts(): List<Product>{
        return productRepository.findAll()
    }

    override fun getProduct(id: String): Optional<Product> {
        return productRepository.findById(id)
    }

    override fun getProductPrice(id: String): Float {
        val product = productRepository.findById(id)
        return if (product.isPresent)
            product.get().price!!
        else
            0.0f
    }

    @Transactional
    override fun addProduct(product: ProductDTO): Product{
        if ( productRepository.getProductByNameAndCategoryAndPrice(product.name!!, product.category!!, product.price!!) != null)
            throw ProductAlreadyExistsException("Product with name ${product.name} already exists")
        try {
            return productRepository.save(mapper.toModel(product))
        }catch (ex: IllegalArgumentException){
            throw BadRequestException("Illegal argument")
        }
    }

    override fun modifyProduct(id: String, newProduct: ProductDTO): Optional<Product> {
        try {
            return productRepository.findById(id)
                .map { product ->
                    product.name = if (newProduct.name != null) newProduct.name else product.name
                    product.category = if(newProduct.category != null) newProduct.category else product.category
                    product.description = if(newProduct.description != null) newProduct.description else product.description
                    product.price = if(newProduct.price != null) newProduct.price else product.price
                    product.picture = if(newProduct.picture != null) newProduct.picture else product.picture
                    productRepository.save(product)
                }
        }catch (ex: IllegalArgumentException){
            throw BadRequestException("Illegal argument")
        }
    }

    override fun deleteProduct(id: String): Optional<Product> {
        try {
            val endpoint = URI.create("http://${HostWarehouseS}/product/${id}")
            val request = RequestEntity<Any>(HttpMethod.DELETE, endpoint)
            restTemplate.exchange(request, String::class.java)
            return productRepository.deleteProductById(id)
        }catch (ex: IllegalArgumentException){
            throw BadRequestException("Illegal argument")
        }
    }

    @KafkaListener(topics = ["warning"], groupId = "catalog")
    fun consumeWarningEvent(@Payload dto: WarningDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaKeys.KEY_PRODUCT_WARNING.value) {
            val admins = userRepository.findByRolesContaining(UserRole.ROLE_ADMIN)
            //println(admins)
            admins.forEach { admin ->
                orderServiceImpl.sendEmail(
                    "[ECOMM][WARNING] Product warning",
                    "Dear Admin ${admin.name} ${admin.surname} the product with id ${dto.idProd} in the warehouse with id ${dto.idWh} crossed the threshold ",
                    admin.email
                )
            }
        }
    }

}