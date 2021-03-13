package com.ecomm.orderservice.dto

import com.ecomm.commons.Order


@org.mapstruct.Mapper
interface  OrderMapper {
    //@Mapping(source = "field", target = "field2")
    fun toDto(order: Order): OrderDTO
    fun toModel(orderDto: OrderDTO): Order
    fun toDtos(orders: List<Order>): List<OrderDTO>
}
