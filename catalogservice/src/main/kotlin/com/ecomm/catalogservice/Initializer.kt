package com.ecomm.catalogservice

import com.ecomm.commons.User
import com.ecomm.commons.UserRole
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.catalogservice.security.PasswordEncoderAndMatcherConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Component

@Component
class Initializer():CommandLineRunner {

    @Autowired
    lateinit var repo:UserRepository

    @Autowired
    lateinit var passwordEncoderAndMatcherConfig: PasswordEncoderAndMatcherConfig

    override fun run(vararg args: String?) {
        val passwenc = passwordEncoderAndMatcherConfig.passwordEncoderAndMatcher()
        repo.deleteAll()
        val user1= User(
            name="Pippo",
            surname = "Pluto",
            email = "luigiferrettino@live.com",
            passw = passwenc.encode("admin"),
            deliveryAddress = "Corso Duca",
            roles = listOf(UserRole.ROLE_CUSTOMER,UserRole.ROLE_ADMIN))
        val user2= User(
            name="Pippone",
            surname = "Plutone",
            email = "ferrettinoluigi@gmail.com",
            passw = passwenc.encode("user"),
            deliveryAddress = "Corso Ducone",
            roles = listOf(UserRole.ROLE_CUSTOMER))
        val user3= User(
            name="Pippino",
            surname = "Plutino",
            email = "luigi.ferrettino@studenti.polito.it",
            passw = passwenc.encode("user"),
            deliveryAddress = "Corso Duchino",
            roles = listOf(UserRole.ROLE_CUSTOMER))
        repo.save(user1)
        repo.save(user2)
        repo.save(user3)
    }
}