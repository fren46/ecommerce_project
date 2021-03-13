package com.ecomm.orderservice.repo

import com.ecomm.commons.Order
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository:MongoRepository<Order,String>{
}