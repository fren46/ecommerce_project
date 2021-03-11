package com.ecomm.catalogservice.repo

import com.ecomm.commons.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository:MongoRepository<User,ObjectId>{
}