package com.ecomm.walletservice.repository
import com.ecomm.commons.Transaction
import org.springframework.data.mongodb.repository.MongoRepository

interface WalletRepository: MongoRepository<Transaction, String> {
    fun getTransactionByBuyerID(buyerID:String):List<Transaction>
    fun getTransactionByOrderID(orderID: String): List<Transaction>
}