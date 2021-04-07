package com.ecomm.orderservice.service

import com.ecomm.commons.Order
import com.ecomm.commons.OrderDTO
import java.util.*

interface OrderService {
    fun createOrder(dto: OrderDTO): Order
    fun getOrder(id: String, userId: String, isAdmin: Boolean): OrderDTO?
    fun cancelOrder(id: String, userId: String): OrderDTO?
    fun modifyOrder(dto: OrderDTO): Optional<Order>
}