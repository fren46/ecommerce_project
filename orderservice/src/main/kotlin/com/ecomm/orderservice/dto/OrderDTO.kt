package com.ecomm.orderservice.dto

data class OrderDTO(
    var id: String? = null,
    var buyer: String? =null,
    var prodList: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var prodPrice: MutableMap<String, Float> = mutableMapOf<String, Float>(),
    var amount: Float = 0.0f,
    var status: String? = null
)
