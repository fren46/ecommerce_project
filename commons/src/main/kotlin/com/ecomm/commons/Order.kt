package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("orders")
class Order(
    @Id
    val id: ObjectId = ObjectId.get(),
    val buyer: User? = null,
    val prodList: Map<Product, Int> = mapOf<Product, Int>(),
    val prodPrice: Map<Product, Float> = mapOf<Product, Float>(),
    val amount: Float? = null,
    val status: OrderStatus? = null
)