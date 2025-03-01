package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.commons.Product
import java.util.*

interface ProductService {
    fun getProducts(): List<Product>
    fun getProduct(id: String): Optional<Product>
    fun addProduct(product: ProductDTO): Product
    fun modifyProduct(id: String, newProduct: ProductDTO): Optional<Product>
    fun deleteProduct(id: String): Optional<Product>
    fun getProductPrice(id: String): Float
}