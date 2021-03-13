package com.ecomm.orderservice.service

import com.ecomm.commons.Order
import com.ecomm.orderservice.dto.OrderDTO
import org.bson.types.ObjectId
import java.util.*

interface OrderService {
    fun createOrder(dto: OrderDTO): Order
    fun getOrder(id: ObjectId): Optional<Order>
    fun cancelOrder(id: ObjectId): Optional<Order>
}