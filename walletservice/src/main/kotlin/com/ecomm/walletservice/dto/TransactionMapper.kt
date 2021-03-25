package com.ecomm.walletservice.dto

import com.ecomm.commons.Transaction

@org.mapstruct.Mapper
interface  TransactionMapper {
    //@Mapping(source = "field", target = "field2")
    fun toDto(transaction: Transaction): TransactionDTO
    fun toModel(transactionDTO: TransactionDTO): Transaction
    //fun toDtos(orders: List<Order>): List<OrderDTO>
}

