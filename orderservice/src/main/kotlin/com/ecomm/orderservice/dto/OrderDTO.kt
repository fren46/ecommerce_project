package com.ecomm.orderservice.dto

import org.bson.types.ObjectId

data class OrderDTO(
    val id: ObjectId = ObjectId.get()

)
