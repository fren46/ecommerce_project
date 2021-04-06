package com.ecomm.catalogservice.repo

import com.ecomm.commons.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository:MongoRepository<User,String>{
    fun findByEmail(email: String):User
    fun findFirstById(id: String): User
}