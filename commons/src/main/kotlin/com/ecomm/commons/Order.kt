package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("orders")
class Order(
    @Id
    var id: String? = ObjectId.get().toHexString() ,
    var buyer: String? = null,
    var prodList: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var prodPrice: MutableMap<String, Float> = mutableMapOf<String, Float>(),
    var amount: Float = 0.0f,
    var status: OrderStatus = OrderStatus.Pending
)