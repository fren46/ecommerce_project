package com.ecomm.catalogservice.dto

import com.ecomm.commons.UserRole
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class UserDTO(
    var email: String,
    var password: String,
    var roles: List<UserRole>
)
