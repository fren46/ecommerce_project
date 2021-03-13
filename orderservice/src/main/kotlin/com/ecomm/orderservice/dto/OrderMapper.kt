package com.ecomm.orderservice.dto

import com.ecomm.commons.Order


@org.mapstruct.Mapper
interface  OrderMapper {
    //@Mapping(source = "field", target = "field2")
    fun toDto(product: Order): OrderDTO
    fun toModel(productDto: OrderDTO): Order
}
