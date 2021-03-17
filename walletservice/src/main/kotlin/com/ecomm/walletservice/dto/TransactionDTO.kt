package com.ecomm.walletservice.dto

data class TransactionDTO (
    var id: String? = null,
    var buyerID: String? =null,
    var amount: Float? = null
)