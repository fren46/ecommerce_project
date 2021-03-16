package com.ecomm.orderservice

import com.ecomm.commons.OrderStatus
import com.ecomm.orderservice.repo.OrderRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
@EnableAsync
class TransactionThread( val repo: OrderRepository) {
    @Async
    @Scheduled(fixedRate = 10000)
    @Throws(InterruptedException::class)
    fun scheduleFixedRateTaskAsync() {
        val a = repo.findAllByStatusOrderByModifiedDateAsc(OrderStatus.Pending)
        if (a.isEmpty())
            return
        println(a)
    }
}