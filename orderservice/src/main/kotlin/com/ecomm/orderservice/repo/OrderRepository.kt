package com.ecomm.orderservice.repo

import com.ecomm.commons.Order
import com.ecomm.commons.OrderStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository:MongoRepository<Order,String>{
    fun findAllByStatus(status: String): List<Order>
    fun findAllByStatusOrderByModifiedDateAsc(status: OrderStatus): List<Order>
    override fun deleteAll()
}