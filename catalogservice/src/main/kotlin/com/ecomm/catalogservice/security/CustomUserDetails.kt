package com.ecomm.catalogservice.security

import com.ecomm.commons.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors

open class CustomUserDetails : User, UserDetails {

    constructor(user: User) : super(
        id = user.id,
		name = user.name,
		surname = user.surname,
		email = user.email,
		passw = user.passw,
        deliveryAddress = user.deliveryAddress,
		roles = user.roles
    )

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return roles.stream().map{role -> SimpleGrantedAuthority(role.toString())}.collect(Collectors.toList())
    }

    override fun getPassword(): String {
        return super.passw
    }


    override fun getUsername(): String {
        return super.name
    }

    override fun isAccountNonExpired(): Boolean {
        return super.accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return super.accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return super.credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return super.enabled
    }

}