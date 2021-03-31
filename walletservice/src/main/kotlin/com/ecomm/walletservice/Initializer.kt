package com.ecomm.walletservice
import com.ecomm.commons.Transaction
import com.ecomm.walletservice.repository.WalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class Initializer:CommandLineRunner {

    @Autowired
    lateinit var repo:WalletRepository

    override fun run(vararg args: String?) {
        //repo.deleteAll()
        //val user1= Transaction(buyerID = "ciao",amount = 2.09,orderID = "eifnrjdj",created = LocalDateTime.now())
        //val user2= Transaction(buyerID = "ciao",amount = 2.48,orderID = "dndee",created = LocalDateTime.now())
        //repo.save(user1)
        //repo.save(user2)



    }
}