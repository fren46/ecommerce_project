package com.ecomm.catalogservice.repo

import com.ecomm.commons.Product
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository: MongoRepository<Product, ObjectId> {
}