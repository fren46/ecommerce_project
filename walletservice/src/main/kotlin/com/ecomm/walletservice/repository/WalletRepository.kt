package com.ecomm.walletservice.repository

import com.ecomm.walletservice.dto.TransactionDTO
import org.springframework.data.mongodb.repository.MongoRepository

interface WalletRepository: MongoRepository<TransactionDTO, String> {
    fun getTransactionByBuyerID(buyerID:String)
}