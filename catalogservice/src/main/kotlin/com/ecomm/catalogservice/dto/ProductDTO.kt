package com.ecomm.catalogservice.dto

import com.ecomm.commons.ProductCategory

data class ProductDTO(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val picture: String? = null,
    val category: ProductCategory? = null,
    val price: Float? = null
)