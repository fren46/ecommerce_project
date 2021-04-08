package com.ecomm.catalogservice

import com.ecomm.commons.User
import com.ecomm.commons.UserRole
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.catalogservice.security.PasswordEncoderAndMatcherConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer:CommandLineRunner {

    @Autowired
    lateinit var repo:UserRepository

    @Autowired
    lateinit var passwordEncoderAndMatcherConfig: PasswordEncoderAndMatcherConfig

    override fun run(vararg args: String?) {
        val passwenc = passwordEncoderAndMatcherConfig.passwordEncoderAndMatcher()
        //repo.deleteAll()
        if (repo.findAll().isEmpty()){
            val user1= User(
                name="Luigi",
                surname = "Ferrettino",
                email = "l.ferrettino@reply.it",
                passw = passwenc.encode("admin"),
                deliveryAddress = "Corso Duca",
                roles = listOf(UserRole.ROLE_CUSTOMER,UserRole.ROLE_ADMIN))
            val user2= User(
                name="Francesco",
                surname = "Valente",
                email = "fr.valente@reply.it",
                passw = passwenc.encode("user"),
                deliveryAddress = "Corso Ducone",
                roles = listOf(UserRole.ROLE_CUSTOMER))
            val user3= User(
                name="Alessandro",
                surname = "Pagliano",
                email = "a.pagliano@reply.it",
                passw = passwenc.encode("user"),
                deliveryAddress = "Corso Duchino",
                roles = listOf(UserRole.ROLE_CUSTOMER))
            repo.save(user1)
            repo.save(user2)
            repo.save(user3)
        }

    }
}