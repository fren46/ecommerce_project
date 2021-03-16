package com.ecomm.orderservice.dto

import java.time.LocalDateTime

data class OrderDTO(
    var id: String? = null,
    var buyer: String? =null,
    var createdDate: LocalDateTime? = null,
    var modifiedDate: LocalDateTime? = null,
    var prodList: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var prodPrice: MutableMap<String, Float> = mutableMapOf<String, Float>(),
    var amount: Float = 0.0f,
    var status: String? = null
)
