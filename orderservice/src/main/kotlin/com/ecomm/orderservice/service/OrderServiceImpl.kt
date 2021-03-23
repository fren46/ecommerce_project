package com.ecomm.orderservice.service

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
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload


@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

    private val mapper = Mappers.getMapper(OrderMapper::class.java)
    private val TOPIC = "status"
    private val KEY_ORDER_CREATED = "ordercreated"
    private val KEY_ORDER_PAID = "orderpaid"
    private val KEY_ORDER_CANCELED = "ordercanceled"


    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>


    fun createFakeOrder(dto: OrderDTO): Order {

        val order = orderRepository.insert(mapper.toModel(OrderDTO(
            id = dto.id,
            buyer = dto.buyer,
            prodList = dto.prodList,
            prodPrice = dto.prodPrice,
            amount = dto.amount,
            status = OrderStatus.Pending.toString(),
            modifiedDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now()
        )))

        this.kafkaTemplate.send(TOPIC, KEY_ORDER_CREATED, mapper.toDto(order))

        return order
    }

    @KafkaListener(topics = ["status"], groupId = "group_id")
    @Throws(IOException::class)
    fun consume(@Payload message: OrderDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if(key == KEY_ORDER_PAID)
            TODO("The order is paid, modify its status and update it into the DB")
        else if(key == KEY_ORDER_CANCELED)
            TODO("The order is canceled, modify its status into the DB. All the other services will receive the info and will perform the rollbacks")
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
        return if(order.isPresent && order.get().status == OrderStatus.Pending) {
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
                                prodList = order.get().prodList,
                                prodPrice = order.get().prodPrice,
                                amount = order.get().amount,
                                status = dto.status ?: order.get().status.toString(),
                                modifiedDate = if(dto.status == order.get().status.toString()) order.get().modifiedDate else LocalDateTime.now(),
                                createdDate = order.get().createdDate
                            )
                        )
                    )
                    val opt = Optional.of(onlyStatus)
                    return opt
                } else
                    return order
            } else
                return order
        } else
            return order as Optional<Order>
    }
}