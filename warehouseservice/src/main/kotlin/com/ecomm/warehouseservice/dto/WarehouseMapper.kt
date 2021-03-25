package com.ecomm.warehouseservice.dto

import com.ecomm.commons.Transaction
import com.ecomm.commons.Warehouse

@org.mapstruct.Mapper
interface  WarehouseMapper {
    //@Mapping(source = "field", target = "field2")
    fun toDto(transaction: Warehouse): WarehouseDTO
    fun toModel(transactionDTO: WarehouseDTO): Warehouse
    //fun toDtos(orders: List<Order>): List<OrderDTO>
}
