package com.ecomm.walletservice.service

import com.ecomm.commons.Transaction
import com.ecomm.walletservice.dto.TransactionDTO

interface WalletService {
    fun getAmount(id: String): Double?
    fun getTransaction(id: String): List<Transaction>?
    fun addTransaction(transactionDTO:TransactionDTO):Double?
}