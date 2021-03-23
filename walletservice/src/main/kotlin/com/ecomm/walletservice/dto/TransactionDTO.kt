package com.ecomm.walletservice.dto

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class TransactionDTO (
    var id: String? = null,
    val buyerID: String?=null,
    val orderID: String?=null,
    val amount: Double?=null,
    val created: LocalDateTime?=null
)