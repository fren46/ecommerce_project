package com.ecomm.orderservice.repo

import com.ecomm.commons.Order
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderRepository:MongoRepository<Order,ObjectId>{
}