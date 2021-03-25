package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
class Transaction(
    @Id
    val id: String? = ObjectId.get().toHexString(),
    var buyerID: String?=null,
    var orderID: String?=null,
    var amount: Double?=null,
    var created: LocalDateTime?=null
)