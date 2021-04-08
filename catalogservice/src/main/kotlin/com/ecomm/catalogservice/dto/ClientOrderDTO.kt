package com.ecomm.catalogservice.dto

data class ClientOrderDTO(
    var buyer: String? = null,
    val prodList: MutableMap<String, Int>? = mutableMapOf<String, Int>(),
    val address: String? = null
)
