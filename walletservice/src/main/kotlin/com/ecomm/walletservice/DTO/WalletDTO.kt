package com.ecomm.walletservice.DTO

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class WalletDTO(
    @Id
    val id: ObjectId =ObjectId.get(),
    val amounth: Float? = null,
    val transactionList: List<Transaction>

)