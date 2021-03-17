package com.ecomm.walletservice.service

import com.ecomm.walletservice.dto.TransactionDTO

interface WalletService {
    fun getAmounth(id: String): Float?
    fun getTransaction(id: String): List<TransactionDTO>?
    fun addTransaction(id: String, transaction:TransactionDTO): Any?
}