package com.ecomm.catalogservice.dto

data class clientOrderDTO(
    var buyer: String? = null,
    val prodList: MutableMap<String, Int>? = mutableMapOf<String, Int>()
)
