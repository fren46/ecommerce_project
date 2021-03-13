package com.ecomm.orderservice.dto

import com.ecomm.commons.OrderStatus
import com.ecomm.commons.Product
import com.ecomm.commons.User
import org.bson.types.ObjectId
import java.util.*
import kotlin.reflect.jvm.internal.impl.resolve.constants.FloatValue

data class OrderDTO(
    val id: String? = null,
    val buyer: User? =null,
    val prodList: Map<Product, Int> = mapOf<Product, Int>(),
    val prodPrice: Map<Product, Float> = mapOf<Product, Float>(),
    val amount: Float? = null,
    val status: OrderStatus? = null
)
