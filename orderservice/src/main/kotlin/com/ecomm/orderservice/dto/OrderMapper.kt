package com.ecomm.orderservice.dto

import com.ecomm.commons.Order
import com.ecomm.commons.OrderDTO


@org.mapstruct.Mapper
interface  OrderMapper {
    fun toDto(order: Order): OrderDTO
    fun toModel(orderDto: OrderDTO): Order
    fun toDtos(order: List<Order>): List<OrderDTO>
}

