package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class Order(
    @Id
    val id: ObjectId = ObjectId.get(),
    val buyer: User,
    val prodList: Dictionary<Product, Int>,
    val prodPrice: Dictionary<Product, Float>,
    val amount: Float,
    val status: OrderStatus
)