package com.ecomm.walletservice.controller

import com.ecomm.commons.Transaction
import com.ecomm.walletservice.dto.TransactionDTO
import com.ecomm.walletservice.service.WalletServiceImpl
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class WalletController (val walletService: WalletServiceImpl) {

    @GetMapping("/{id}/wallet")
    @ApiOperation(value = "Return the wallet amount of a given buyer")
    fun getAmount(@PathVariable id:String):ResponseEntity<Double?>{
        val amount= walletService.getAmount(id)
        return if (amount!= null)
            ResponseEntity(amount, HttpStatus.OK)
        else
            ResponseEntity(0.0, HttpStatus.OK)
    }

    @GetMapping("/{id}/transaction")
    @ApiOperation(value = "Return all the transaction for a given buyer")
    fun getTransaction(@PathVariable id:String): ResponseEntity<List<Transaction>>{
        val transactionList= walletService.getTransaction(id)
        return if (transactionList != emptyList<Transaction>())
            ResponseEntity(transactionList, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @PostMapping("/transaction/add")
    @ApiOperation(value = "Add a transaction")
    fun addTransaction(@RequestBody t: TransactionDTO): ResponseEntity<Double?> {
        val amount = walletService.addTransaction(t)
        return if (amount != null)
            ResponseEntity(amount, HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }

}

