package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document
open class User(
    @Id
    var id: ObjectId = ObjectId.get(),
    var name: String,
    var surname:String,
    var email: String,
    var deliveryAddress:String?,
    var roles: List<UserRole>,
    var passw: String
){
    var accountNonExpired: Boolean = true
	var accountNonLocked: Boolean = true
	var credentialsNonExpired: Boolean = true
	var enabled: Boolean = true
//    constructor(user: User) : this(user.id, user.name, user.surname, user.email, user.deliveryAddress, user.roles, user.passw) {
//		id = user.id
//		name = user.name
//		surname = user.surname
//		email = user.email
//		passw = user.passw
//        accountNonExpired = true
//        accountNonLocked = true
//        credentialsNonExpired = true
//        enabled = true
//        deliveryAddress = user.deliveryAddress
//		roles = user.roles
//	}

}