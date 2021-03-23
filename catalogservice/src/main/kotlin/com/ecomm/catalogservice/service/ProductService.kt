package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.commons.Product
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.bson.types.ObjectId
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

import java.util.*

@Mapper
interface ProductMapper {
    fun toDto(product: Product): ProductDTO
    fun toModel(productDTO: ProductDTO): Product
}

@Service
class ProductService (
    private val productRepository: ProductRepository
    ) {

    private val TOPIC: String = "test"

    private val mapper = Mappers.getMapper(ProductMapper::class.java);

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, ProductDTO>

    fun getProducts(): List<Product>{
        return productRepository.findAll()
    }

    fun getProduct(id: ObjectId): Optional<Product> {
        return productRepository.findById(id)
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