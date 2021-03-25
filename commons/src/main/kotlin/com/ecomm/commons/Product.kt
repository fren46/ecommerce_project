package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Product(
    @Id
    var id: ObjectId? = ObjectId.get(),
    var name: String,
    var description: String?,
    var picture: String?,
    var category: ProductCategory,
    var price: Float
)