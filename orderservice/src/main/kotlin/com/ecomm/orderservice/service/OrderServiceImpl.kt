package com.ecomm.orderservice.service

import com.ecomm.commons.Order
import com.ecomm.commons.OrderStatus
import com.ecomm.orderservice.dto.OrderDTO
import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.orderservice.repo.OrderRepository
import org.bson.types.ObjectId
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderServiceImpl(private val orderRepository: OrderRepository): OrderService {

        private val mapper = Mappers.getMapper(OrderMapper::class.java);

        override fun createOrder(dto: OrderDTO): Order {

            return orderRepository.save(mapper.toModel(dto))
        }

        override fun getOrder(id: ObjectId): Optional<Order> {
            println("===> ${id}")
            return orderRepository.findById(id)
        }

        override fun cancelOrder(id: ObjectId): Optional<Order> {
            val order = orderRepository.findById(id)
            if (order.isPresent.and(order.get().status == OrderStatus.Paid)) {
                orderRepository.deleteById(id)
                return order
            } else
                return order
        }
}