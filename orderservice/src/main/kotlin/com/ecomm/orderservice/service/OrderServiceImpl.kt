package com.ecomm.orderservice.service

import com.ecomm.commons.*
import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.orderservice.repo.OrderRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.beans.factory.annotation.Autowired
import java.io.IOException

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.transaction.annotation.Transactional


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository, private val emailSender: JavaMailSender): OrderService {

    private val mapper = Mappers.getMapper(OrderMapper::class.java)


    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>

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

    override fun createOrder(dto: OrderDTO): Order {

        val order = orderRepository.save(
            mapper.toModel(
                OrderDTO(
                    id = dto.id,
                    buyer = dto.buyer,
                    prodList = dto.prodList,
                    prodPrice = dto.prodPrice,
                    amount = dto.amount,
                    status = OrderStatus.Pending.toString(),
                    modifiedDate = LocalDateTime.now(),
                    createdDate = LocalDateTime.now()
                )
            )
        )
        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_CREATED.value, mapper.toDto(order))

        return order
    }

    @KafkaListener(topics = ["status"], groupId = "order")
    @Throws(IOException::class)
    @Transactional
    fun consume(@Payload dto: OrderDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaKeys.KEY_ORDER_PAID.value) {
            if (dto.whrecord.isEmpty().or(dto.transactionId.isNullOrBlank())) {
                this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_FAILED.value, dto)
                return
            }
            val order = dto.id?.let { orderRepository.findById(it) }
            if (order != null) {
                val saved = orderRepository.save(
                    mapper.toModel(
                        OrderDTO(
                            id = dto.id,
                            buyer = dto.buyer ?: order.get().buyer,
                            transactionId = dto.transactionId ?: order.get().transactionId,
                            whrecord = if (dto.whrecord.isEmpty()) order.get().whrecord else dto.whrecord,
                            prodList = if (dto.prodList.isEmpty()) order.get().prodList else dto.prodList,
                            prodPrice = if (dto.prodPrice.isEmpty()) order.get().prodPrice else dto.prodPrice,
                            amount = dto.amount ?: order.get().amount,
                            status = OrderStatus.Issued.toString(),
                            modifiedDate = LocalDateTime.now(),
                            createdDate = order.get().createdDate
                        )
                    )
                )
                val converted = mapper.toDto(saved)
                println("PAID: $converted")
                //sendEmail("[ECOMM][UPDATE] Your order is PAID", "Dear customer, \nYour order #${dto.id} is PAID", "ferrettinoluigi@gmail.com")
                return
            }
        }
        else if (key == KafkaKeys.KEY_ORDER_CANCELED.value) {
            val order = dto.id?.let { orderRepository.findById(it) }
            if (order!!.isPresent) {
                val saved = orderRepository.save(
                    mapper.toModel(
                        OrderDTO(
                            id = order.get().id,
                            buyer = order.get().buyer,
                            transactionId = order.get().transactionId,
                            whrecord = order.get().whrecord,
                            prodList = order.get().prodList,
                            prodPrice = order.get().prodPrice,
                            amount = order.get().amount,
                            status = OrderStatus.Canceled.toString(),
                            modifiedDate = LocalDateTime.now(),
                            createdDate = order.get().createdDate
                        )
                    )
                )
                val converted = mapper.toDto(saved)
                println("CANCELED: $converted")
                //sendEmail("[ECOMM][UPDATE] Your order is CANCELED", "Dear customer, \nYour order #${dto.id} is CANCELED. The refund is on its way.", "ferrettinoluigi@gmail.com")
                return
            }
        }
        else if (key == KafkaKeys.KEY_ORDER_FAILED.value) {
            val order = dto.id?.let { orderRepository.findById(it) }
            if (order!!.isPresent) {
                val saved = orderRepository.save(
                    mapper.toModel(
                        OrderDTO(
                            id = order.get().id,
                            buyer = order.get().buyer,
                            transactionId = order.get().transactionId,
                            whrecord = order.get().whrecord,
                            prodList = order.get().prodList,
                            prodPrice = order.get().prodPrice,
                            amount = order.get().amount,
                            status = OrderStatus.Failed.toString(),
                            modifiedDate = LocalDateTime.now(),
                            createdDate = order.get().createdDate
                        )
                    )
                )
                val converted = mapper.toDto(saved)
                println("FAILED: $converted")
                //sendEmail("[ECOMM][UPDATE] Your order is FAILED", "Dear customer, \nYour order #${dto.id} is FAILE. You'll be refunded ASAP.", "ferrettinoluigi@gmail.com")
                return
            }
        }
        else if (key == KafkaKeys.KEY_ORDER_CREATED.value) {
            println("CREATED: $dto")
            return
        }
        else if (key == KafkaKeys.KEY_ORDER_AVAILABLE.value) {
            println("AVAILABLE: $dto")
            return
        }
    }

    fun getOrders(): List<Order> {

        return orderRepository.findAll()
    }

    override fun getOrder(id: String): Optional<Order> {
        println("Requested => ${id}")
        return orderRepository.findById(id)
    }

    @Transactional
    override fun cancelOrder(id: String, userId: String): OrderDTO? {
        val order = orderRepository.findById(id)
        if(order.isPresent && order.get().buyer != userId){
            return null
        }
        return if (order.isPresent
                .and((order.get().status == OrderStatus.Issued)
                .or(order.get().status == OrderStatus.Pending))) {
            val modified = orderRepository.save(
                mapper.toModel(
                    OrderDTO(
                        id = order.get().id,
                        buyer = order.get().buyer,
                        transactionId = order.get().transactionId,
                        whrecord = order.get().whrecord,
                        prodList = order.get().prodList,
                        prodPrice = order.get().prodPrice,
                        amount = order.get().amount,
                        status = OrderStatus.Canceled.toString(),
                        modifiedDate = LocalDateTime.now(),
                        createdDate = order.get().createdDate
                    )
                )
            )
            val result = this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_CANCELED.value, mapper.toDto(modified))

            mapper.toDto(modified)
        } else
            null
    }



    @Transactional
    override fun modifyOrder(dto: OrderDTO): Optional<Order> {
        val order = dto.id?.let { orderRepository.findById(it) }
        if (order != null) {
            if (!OrderStatus.values().any { it.name != dto.status })
                return order
            if (order.isPresent) {
                if (order.get().status == OrderStatus.Pending) {
                    val modified = orderRepository.save(
                        mapper.toModel(
                            OrderDTO(
                                id = order.get().id,
                                buyer = dto.buyer ?: order.get().buyer,
                                transactionId = dto.transactionId ?: order.get().transactionId,
                                whrecord = if (dto.whrecord.isEmpty()) order.get().whrecord else dto.whrecord,
                                prodList = if (dto.prodList.isEmpty()) order.get().prodList else dto.prodList,
                                prodPrice = if (dto.prodPrice.isEmpty()) order.get().prodPrice else dto.prodPrice,
                                amount = dto.amount ?: order.get().amount,
                                status = dto.status ?: order.get().status.toString(),
                                modifiedDate = LocalDateTime.now(),
                                createdDate = order.get().createdDate
                            )
                        )
                    )
                    val opt = Optional.of(modified)
                    return opt
                } else if ((order.get().status != OrderStatus.Canceled).and(order.get().status != OrderStatus.Failed)) {
                    val onlyStatus = orderRepository.save(
                        mapper.toModel(
                            OrderDTO(
                                id = order.get().id,
                                buyer = order.get().buyer,
                                transactionId = order.get().transactionId,
                                whrecord = order.get().whrecord,
                                prodList = order.get().prodList,
                                prodPrice = order.get().prodPrice,
                                amount = order.get().amount,
                                status = dto.status ?: order.get().status.toString(),
                                modifiedDate = if (dto.status == order.get().status.toString()) order.get().modifiedDate else LocalDateTime.now(),
                                createdDate = order.get().createdDate
                            )
                        )
                    )
                    if (onlyStatus.status == OrderStatus.Canceled) {
                        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_CANCELED.value, mapper.toDto(onlyStatus))
                    } else if(onlyStatus.status == OrderStatus.Delivered) {
                        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_DELIVERED.value, mapper.toDto(onlyStatus))
                    } else if(onlyStatus.status == OrderStatus.Delivering) {
                        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_DELIVERING.value, mapper.toDto(onlyStatus))
                    }
                    val opt = Optional.of(onlyStatus)
                    return opt
                } else
                    return order
            } else
                return order
        }
        return order as Optional<Order>
    }
}