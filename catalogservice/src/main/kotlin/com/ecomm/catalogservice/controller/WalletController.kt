package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.TransactionDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.WalletNotFoundException
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.catalogservice.security.CustomUserDetails
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("/wallet")
class WalletController(
    private val userRepository: UserRepository
) {

    @Value("\${application.urlWalletService}")
    private lateinit var HostWalletS: String
    val restTemplate = RestTemplate()

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns the money on the wallet of logged user")
    fun getLoggedUserWallet(): Double{
        val auth = SecurityContextHolder.getContext().authentication
        val userDetail = auth.principal as CustomUserDetails
        val res = restTemplate.exchange(
            RequestEntity<Any>(HttpMethod.GET, URI.create("http://${HostWalletS}/${userDetail.id}/wallet")),
            Double::class.java
        )
        return res.body!!
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns the money on the wallet of user by user id")
    fun getWallet(
        @PathVariable
        @ApiParam(value = "User id", required = true)
        id: String
    ): Double{
        try {
            val user = userRepository.findById(id)
            if (user.isPresent){
                val res = restTemplate.exchange(
                    RequestEntity<Any>(HttpMethod.GET, URI.create("http://${HostWalletS}/${id}/wallet")),
                    Double::class.java
                )
                if (res.statusCode == HttpStatus.OK && res.body!= null)
                    return res.body!!
                else
                    throw WalletNotFoundException("Wallet of user with id $id not found")
            }else{
                throw BadRequestException("User not found")
            }
        }catch (ex: RestClientException){
            throw WalletNotFoundException("Wallet of user with id $id not found")
        }
    }

    @PostMapping("/{id}/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add transaction to user wallet")
    fun addProductQuantity(
        @PathVariable
        @ApiParam(value = "User id", required = true)
        id: String,
        @RequestBody
        @ApiParam(value = "Amount of the transaction", required = true)
        amount: String
    ): Double{
        try {
            val user = userRepository.findById(id)
            if (user.isPresent){
                val value: Double = amount.toDouble()
                val transactionDTO = TransactionDTO(id, value)
                val res = restTemplate.exchange(
                    RequestEntity<Any>(transactionDTO, HttpMethod.POST, URI.create("http://${HostWalletS}/transaction/add")),
                    Double::class.java
                )
                if (res.statusCode == HttpStatus.OK && res.body!= null)
                    return res.body!!
                else
                    throw WalletNotFoundException("Wallet of user with id $id not found")
            }else{
                throw BadRequestException("User not found")
            }
        }catch (ex: NumberFormatException){
            throw BadRequestException("The body of the request must be a double number")
        }catch (ex: RestClientException){
            throw WalletNotFoundException("2 Wallet of user with id $id not found")
        }
    }

}