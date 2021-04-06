package com.ecomm.catalogservice.service

import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.OrderDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class OrderServiceImpl(private val emailSender: JavaMailSender, private val userRepository: UserRepository): OrderService {

    //@Autowired
    //lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>

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
        @Payload dto: OrderDTO,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String
    ) {
        println("porco schifo ho fallito dto: "+dto)
        if(key == KafkaKeys.KEY_ORDER_PAID.value){
            var customer = userRepository.findFirstById(dto.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is PAID",
                "Dear Customer ${customer.name} ${customer.surname} your order with id ${dto.id} is PAID",
                customer.email
            )
        }else if(key == KafkaKeys.KEY_ORDER_FAILED.value){
            // TODO: 4/6/2021 make different failed key for each kind of error to notify the user with the correct error (not money or not availability)
            var customer = userRepository.findFirstById(dto.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is FAILED",
                "Dear Customer ${customer.name} ${customer.surname} your order with id ${dto.id} is FAILED",
                customer.email
            )
        }else if(key == KafkaKeys.KEY_ORDER_DELIVERED.value){
            var customer = userRepository.findFirstById(dto.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is DELIVERED",
                "Dear Customer ${customer.name} ${customer.surname} your order with id ${dto.id} is DELIVERED",
                customer.email
            )
        }else if(key == KafkaKeys.KEY_ORDER_DELIVERING.value){
            var customer = userRepository.findFirstById(dto.buyer!!)
            sendEmail(
                "[ECOMM][UPDATE] Your order is DELIVERING",
                "Dear Customer ${customer.name} ${customer.surname} your order with id ${dto.id} is DELIVERING",
                customer.email
            )
        }
    }
}