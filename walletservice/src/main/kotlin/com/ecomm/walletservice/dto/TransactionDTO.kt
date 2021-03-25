package com.ecomm.walletservice.dto

import org.bson.types.ObjectId
import java.time.LocalDateTime

data class TransactionDTO (
    var id: String? = null,
    var buyerID: String?=null,
    var orderID: String?=null,
    var amount: Double?=null,
    var created: LocalDateTime?=null
)