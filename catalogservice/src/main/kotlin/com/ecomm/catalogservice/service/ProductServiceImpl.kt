package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.ProductAlreadyExistsException
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.commons.Product
import org.bson.types.ObjectId
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.expression.common.ExpressionUtils.toFloat
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

import java.util.*
import kotlin.jvm.Throws

@Mapper
interface ProductMapper {
    fun toDto(product: Product): ProductDTO
    fun toModel(productDTO: ProductDTO): Product
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
    ): ProductService {

    private val TOPIC: String = "test"

    private val mapper = Mappers.getMapper(ProductMapper::class.java);

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, ProductDTO>

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
            return productRepository.deleteProductById(id)
        }catch (ex: IllegalArgumentException){
            throw BadRequestException("Illegal argument")
        }
    }

    fun produceTestEvent(product: ProductDTO){
        kafkaTemplate.send(TOPIC, product)
    }

    @KafkaListener(
        topics = ["test"],
        groupId = "catalog")
    fun consumeTestEvent(product: ProductDTO){
        print(product)
    }
}