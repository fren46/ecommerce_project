package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Wallet(
    @Id
    val id: ObjectId =ObjectId.get(),
    val amount: Float,
    val transactionList: List<Transaction>

)