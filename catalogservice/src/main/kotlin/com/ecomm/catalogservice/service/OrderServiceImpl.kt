package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.OrderDTO
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(private val emailSender: JavaMailSender, private val userRepository: UserRepository): OrderService {

    fun sendEmail(
        subject: String,
        text: String,
        targetEmail: String
    ) {
        val message = SimpleMailMessage()
        message.setSubject(subject)
        message.setText(text)
        message.setTo(targetEmail)
        emailSender.send(message)
    }

    @KafkaListener(topics = ["status"], groupId = "catalog")
    override fun notifyClient(
        @Payload order: OrderDTO,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) str: String
    ) {
        if(str == KafkaKeys.KEY_ORDER_PAID.value){
            val customer = userRepository.findFirstById(order.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is PAID",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is PAID.",
                customer.email
            )
        }else if(str == KafkaKeys.KEY_ORDER_FAILED.value){
            val customer = userRepository.findFirstById(order.buyer!!)
            if (order.whrecord.isNullOrEmpty()){
                sendEmail(
                "[ECOMM][UPDATE] Your order is FAILED",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is FAILED due to products availability.",
                customer.email
                )
            }else if (order.transactionId.isNullOrBlank()){
                sendEmail(
                "[ECOMM][UPDATE] Your order is FAILED",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is FAILED due to wallet availability. Recharge your wallet.",
                customer.email
                )
            }else{
                sendEmail(
                "[ECOMM][UPDATE] Your order is FAILED",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is FAILED.",
                customer.email
                )
            }
        }else if(str == KafkaKeys.KEY_ORDER_DELIVERED.value){
            val customer = userRepository.findFirstById(order.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is DELIVERED",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is DELIVERED.",
                customer.email
            )
        }else if(str == KafkaKeys.KEY_ORDER_DELIVERING.value){
            val customer = userRepository.findFirstById(order.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is DELIVERING",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is DELIVERING.",
                customer.email
            )
        }else if(str == KafkaKeys.KEY_ORDER_CANCELED.value){
            val customer = userRepository.findFirstById(order.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is CANCELED",
                "Dear Customer ${customer.name} ${customer.surname}, \nYour order with id ${order.id} is CANCELED.",
                customer.email
            )
        }
    }
}