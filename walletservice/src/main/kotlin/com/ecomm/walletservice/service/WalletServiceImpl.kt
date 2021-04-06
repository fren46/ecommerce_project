package com.ecomm.walletservice.service
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.OrderDTO
import com.ecomm.commons.Transaction
import com.ecomm.walletservice.dto.TransactionDTO
import com.ecomm.walletservice.dto.TransactionMapper
import com.ecomm.walletservice.repository.WalletRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDateTime

@Service
class WalletServiceImpl(private val repo:WalletRepository): WalletService {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>
    private val mapper = Mappers.getMapper(TransactionMapper::class.java)

    override fun getAmount(id: String): Double? {
        val transactionList = repo.getTransactionByBuyerID(id)
        if (transactionList!= emptyList<Transaction>()) {
            val amountList = mutableListOf<Double>()
            transactionList.forEach { amountList.add(it.amount!!) }
            return Math.round(amountList.sum() * 100) / 100.0
        }
        return null
    }

    override fun getTransaction(id: String): List<Transaction> {
        if (repo.getTransactionByBuyerID(id)!= emptyList<Transaction>()) {
            return repo.getTransactionByBuyerID(id)
        }
        return emptyList()
    }

    override fun addTransaction(transactionDTO: TransactionDTO): String {

        val transaction = mapper.toModel(transactionDTO)
        transaction.created = LocalDateTime.now()
        //val orderDTO=OrderDTO(buyer = "ciao",amount = 4.57f,id = "cadnde")
        //this.kafkaTemplate.send("status", KafkaKeys.KEY_ORDER_AVAILABLE.value, orderDTO)
        if (transaction.orderID.isNullOrBlank())
            transaction.orderID = "recharge"
        repo.save(transaction)
        return transaction.id!!
    }

    @KafkaListener(topics = ["status"], groupId = "wallet")
    @Throws(IOException::class)
    fun consume(@Payload order: OrderDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if(key==KafkaKeys.KEY_ORDER_AVAILABLE.value) {
                if (Math.round((order.amount!!).toDouble() * 100) / 100.0 <= getAmount(order.buyer!!)!! && getAmount(order.buyer!!)!= null) {
                    val transactionDTO = TransactionDTO(
                        buyerID = order.buyer,
                        amount = -Math.round((order.amount!!).toDouble() * 100) / 100.0, //The transaction has a negative amount then buying
                        created = LocalDateTime.now(),
                        orderID = order.id
                    )
                    val transaction=mapper.toModel(transactionDTO)
                    repo.save(transaction)
                    println("Transaction " + transaction.id + " added")
                    order.transactionId = transaction.id
                    this.kafkaTemplate.send("status", KafkaKeys.KEY_ORDER_PAID.value, order)
                }

                else {

                    this.kafkaTemplate.send("status", KafkaKeys.KEY_ORDER_FAILED.value, order)
                    println("Order "  + order.id + " failed")
                }
        }
            else if (key== KafkaKeys.KEY_ORDER_CANCELED.value) {
                if (order.transactionId.isNullOrBlank()) {
                    return
                }
                val result = order.id?.let { repo.getTransactionByOrderID(it) }
                if(result != null) {
                    if ((result.size > 1).or(result.size < 1)) {
                        return
                    }
                    else  {

                        val transactionDTO = TransactionDTO(
                            buyerID = result[0].buyerID,
                            amount = Math.round((order.amount!!).toDouble() * 100) / 100.0,
                            created = LocalDateTime.now(),
                            orderID = order.id
                        )

                        repo.save(mapper.toModel(transactionDTO))
                        println("Order " + order.id + " Refunded")
                    }
                }

            }
        }
    }