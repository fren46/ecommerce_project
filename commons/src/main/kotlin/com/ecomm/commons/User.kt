package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document
class User(
    @Id
    val id: ObjectId = ObjectId.get(),
    val name: String,
    val surname:String,
    val email: String,
    val deliveryAddress:String?,
    val role: UserRole
)