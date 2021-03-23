package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Transaction(
    @Id
    var id: String? = ObjectId.get().toString(),
    val buyerID: String?=null,
    val orderID: String?=null,
    val amount: Double?=null,
    val created: LocalDateTime?=null
)