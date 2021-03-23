package com.ecomm.catalogservice.dto

import com.ecomm.commons.ProductCategory
import org.bson.types.ObjectId

data class ProductDTO(
    val id: ObjectId?,
    val name: String? = null,
    val description: String? = null,
    val picture: String? = null,
    val category: ProductCategory? = null,
    val price: Float? = null
)