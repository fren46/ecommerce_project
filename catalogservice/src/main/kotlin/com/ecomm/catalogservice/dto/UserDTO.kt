package com.ecomm.catalogservice.dto

import com.ecomm.commons.UserRole

data class UserDTO (
    var id: String?,
    var name: String?,
    var surname:String?,
    var email: String?,
    var deliveryAddress:String?,
    var roles: List<UserRole>,
){}