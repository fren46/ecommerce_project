package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Product(
    @Id
    val id: ObjectId = ObjectId.get(),
    val name: String,
    val description: String,
    val picture: String,
    val category: ProductCategory,
    var price: Float
)