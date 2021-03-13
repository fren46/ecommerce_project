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
import java.util.*

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

        private val mapper = Mappers.getMapper(OrderMapper::class.java);

        fun createFakeOrder(dto: OrderDTO): Order {
            return orderRepository.save(mapper.toModel(dto))
        }

        fun getOrders(): List<Order> {
            return orderRepository.findAll()
        }

        override fun createOrder(dto: OrderDTO): Order {

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
}