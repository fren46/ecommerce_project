package com.ecomm.orderservice.service

import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.Order
import com.ecomm.commons.OrderStatus
import com.ecomm.orderservice.dto.OrderDTO
import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.orderservice.repo.OrderRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.JSONPObject
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.apache.kafka.clients.producer.ProducerRecord
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.beans.factory.annotation.Autowired
import java.io.IOException

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

    private val mapper = Mappers.getMapper(OrderMapper::class.java)
    private val TOPIC = "status"


    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>


    fun createFakeOrder(dto: OrderDTO): Order {

        val order = orderRepository.insert(
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
        this.kafkaTemplate.send(TOPIC, KafkaKeys.KEY_ORDER_CREATED.value, mapper.toDto(order))

        return order
    }

    @KafkaListener(topics = ["status"], groupId = "group_id")
    @Throws(IOException::class)
    fun consume(@Payload dto: OrderDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaKeys.KEY_ORDER_PAID.value) {
            val saved = orderRepository.save(mapper.toModel(
                OrderDTO(
                    id = dto.id,
                    buyer = dto.buyer,
                    transactionId = dto.transactionId,
                    whrecord = dto.whrecord,
                    prodList = dto.prodList,
                    prodPrice = dto.prodPrice,
                    amount = dto.amount,
                    status = "Paid",
                    modifiedDate = LocalDateTime.now(),
                    createdDate = dto.createdDate
            )
            ))
            val converted = mapper.toDto(saved)
            println("PAID: $converted")
        }
        else if (key == KafkaKeys.KEY_ORDER_CANCELED.value)
            println("CANCELED: $dto")
        else if (key == KafkaKeys.KEY_ORDER_CREATED.value)
            println("CREATED: $dto")
        else if (key == KafkaKeys.KEY_ORDER_AVAILABLE.value)
            println("AVAILABLE: $dto")
    }

    fun getOrders(): List<Order> {

        return orderRepository.findAll()
    }

    override fun createOrder(dto: OrderDTO): Order {
        //TODO Implement correctly the transaction order by consulting the endpoints of wallet and warehouse.

        return orderRepository.save(mapper.toModel(dto))
    }

    override fun getOrder(id: String): Optional<Order> {
        println("===> ${id}")
        return orderRepository.findById(id)
    }

    override fun cancelOrder(id: String): Boolean {
        val order = orderRepository.findById(id)
        return if (order.isPresent && order.get().status == OrderStatus.Pending) {
            orderRepository.deleteById(id)
            true
        } else
            false
    }

    override fun modifyOrder(dto: OrderDTO): Optional<Order> {
        /* For testing purposes
        val orders = dto.status?.let { orderRepository.findAllByStatus(it) }
        if (orders != null) {
            for (o in orders)
                print("\n" + o.id + "\n")
        }
    */
        val order = dto.id?.let { orderRepository.findById(it) }
        if (order != null) {
            if (order.isPresent) {
                if (order.get().status == OrderStatus.Pending) {
                    val modified = orderRepository.save(
                        mapper.toModel(
                            OrderDTO(
                                id = dto.id,
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
                } else if (order.get().status != OrderStatus.Canceled) {
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
                        this.kafkaTemplate.send(TOPIC, KafkaKeys.KEY_ORDER_CANCELED.value, mapper.toDto(onlyStatus))
                    }
                    val opt = Optional.of(onlyStatus)
                    return opt
                } else
                    return order
            } else
                return order as Optional<Order>
        }
        return order as Optional<Order>
    }
}