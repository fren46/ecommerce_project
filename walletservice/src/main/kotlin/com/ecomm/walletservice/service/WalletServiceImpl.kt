package com.ecomm.walletservice.service
import com.ecomm.commons.OrderDTO
import com.ecomm.commons.Transaction
import com.ecomm.walletervice.dto.TransactionMapper
import com.ecomm.walletservice.dto.TransactionDTO
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
import kotlin.math.round


@Service
class WalletServiceImpl(private val repo:WalletRepository): WalletService {

    private val mapper = Mappers.getMapper(TransactionMapper::class.java)

    private val TOPIC = "status"
    private val KEY_ORDER_CREATED = "order created"
    private val KEY_ORDER_PAID = "order paid"
    private val KEY_ORDER_CANCELED = "order canceled"

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>

    override fun getAmount(id: String): Double? {
        val transactionList = repo.getTransactionByBuyerID(id)
        val amountList = mutableListOf<Double>()
        transactionList.forEach { amountList.add(it.amount!!) }
        return amountList.sum()
    }

    override fun getTransaction(id: String): List<Transaction>? {
        return repo.getTransactionByBuyerID(id)
    }

    override fun addTransaction(transactionDTO: TransactionDTO): String {
        val transaction = mapper.toModel(transactionDTO)
        repo.save(transaction)
        return transaction.id!!
    }

    @KafkaListener(topics = ["status"], groupId = "group_id")
    @Throws(IOException::class)
    fun consume(@Payload message: String, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        val jsonMapper = ObjectMapper()
        jsonMapper.registerModule(JavaTimeModule())
        print(key)
        val order = jsonMapper.readValue(message, OrderDTO::class.java)
        if(key==KEY_ORDER_CREATED && order.wHRecord != emptyMap<String, Int>()) {
            val transaction = Transaction(
                buyerID = order.buyer,
                amount = -order.amount!!.toDouble(), //The transaction has a negative amount then buying
                created = LocalDateTime.now(),
                orderID = order.id
            )
            repo.save(transaction)
            println("Transaction " + transaction.id + " added")
            order.transactionId = transaction.id
            this.kafkaTemplate.send(TOPIC, KEY_ORDER_PAID, order)
        }
        else if (key==KEY_ORDER_CANCELED) {
            val transaction = Transaction(
                buyerID = order.buyer,
                amount = -order.amount!!.toDouble(),
                created = LocalDateTime.now(),
                orderID = order.id
            )
            repo.save(transaction)
            println("Order Refunded")
        }
   }
}