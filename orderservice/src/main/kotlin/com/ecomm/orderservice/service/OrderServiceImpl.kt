package com.ecomm.orderservice.service

import com.ecomm.commons.Order
import com.ecomm.commons.OrderStatus
import com.ecomm.orderservice.dto.OrderDTO
import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.orderservice.repo.OrderRepository
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime
import java.util.*

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

    private val mapper = Mappers.getMapper(OrderMapper::class.java);

    fun createFakeOrder(dto: OrderDTO): Order {

        return orderRepository.insert(mapper.toModel(OrderDTO(
            id = dto.id,
            buyer = dto.buyer,
            prodList = dto.prodList,
            prodPrice = dto.prodPrice,
            amount = dto.amount,
            status = dto.status,
            modifiedDate = LocalDateTime.now(),
            createdDate = LocalDateTime.now()
        )))
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
        return if (order != null) {
            if(order.isPresent && order.get().status == OrderStatus.Pending) {
                val modified = orderRepository.save(mapper.toModel(
                    OrderDTO(
                        id = dto.id,
                        buyer = dto.buyer,
                        prodList = dto.prodList,
                        prodPrice = dto.prodPrice,
                        amount = dto.amount,
                        status = dto.status,
                        modifiedDate = LocalDateTime.now()
                )))
                val opt = Optional.of(modified)
                opt
            } else
                order
        } else
            order as Optional<Order>
        }
}