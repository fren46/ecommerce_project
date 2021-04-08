package com.ecomm.commons

import java.time.LocalDateTime

data class OrderDTO(
    var id: String? = null,
    var buyer: String? =null,
    var createdDate: LocalDateTime? = null,
    var modifiedDate: LocalDateTime? = null,
    var transactionId: String? = null,
    var whrecord: MutableMap<String, MutableMap<String, Int>> = mutableMapOf(),
    var prodList: MutableMap<String, Int> = mutableMapOf(),
    var prodPrice: MutableMap<String, Float> = mutableMapOf(),
    var amount: Float? = null,
    var status: String? = null,
    var address: String? = null
)