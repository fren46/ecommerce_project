package com.ecomm.walletservice.controller

import com.ecomm.commons.Transaction
import com.ecomm.walletservice.dto.TransactionDTO
import com.ecomm.walletservice.service.WalletServiceImpl
import org.springframework.web.bind.annotation.*

@RestController
class WalletController (val walletService: WalletServiceImpl) {

    @GetMapping("/{id}/wallet")
    fun getAmount(@PathVariable id:String): Double{
        return walletService.getAmount(id)
    }

    @GetMapping("/{id}/transaction")
    fun getTransaction(@PathVariable id:String): List<Transaction>{
        return walletService.getTransaction(id)
    }

    @PostMapping("/transaction/add")
    fun addTransaction(@RequestBody t: TransactionDTO?): String {
        val id = walletService.addTransaction(t!!)
        return("Transaction " + id + " added")
    }
}

