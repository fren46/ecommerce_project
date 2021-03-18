package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.commons.Product
import org.bson.types.ObjectId
import org.mapstruct.Mapper
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

    fun getProducts(): List<Product>{
        return productRepository.findAll()
    }

    fun getProduct(id: ObjectId): Optional<Product> {
        return productRepository.findById(id)
    }
}