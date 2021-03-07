package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class Warehouse(
    @Id
    val id: ObjectId = ObjectId.get(),
    val name: String,
    val stocks: Dictionary<Product, Int>
)