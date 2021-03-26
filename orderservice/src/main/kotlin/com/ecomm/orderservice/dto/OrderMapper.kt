package com.ecomm.orderservice.dto

import com.ecomm.commons.Order
import com.ecomm.commons.OrderDTO


@org.mapstruct.Mapper
interface  OrderMapper {
    //@Mapping(source = "field", target = "field2")
    fun toDto(order: Order): OrderDTO
    fun toModel(orderDto: OrderDTO): Order
    fun toDtos(order: List<Order>): List<OrderDTO>
}

