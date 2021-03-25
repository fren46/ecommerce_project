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

    override fun getProduct(id: ObjectId): Optional<Product> {
        return productRepository.findById(id)
    }

    override fun addProduct(product: ProductDTO): Product{
        if ( productRepository.getProductByNameAndCategoryAndPrice(product.name, product.category, product.price) != null)
            throw ProductAlreadyExistsException("Product with name ${product.name} already exists")
        try {
            return productRepository.save(mapper.toModel(product))
        }catch (ex: IllegalArgumentException){
            throw BadRequestException("Illegal argument")
        }

    }

    fun produceTestEvent(product: ProductDTO){
        kafkaTemplate.send(TOPIC, product)
    }

    @KafkaListener(
        topics = ["test"],
        groupId = "group_id",
        containerFactory = "ProductJsonListener")
    fun consumeTestEvent(product: ProductDTO){
        print(product)
    }
}