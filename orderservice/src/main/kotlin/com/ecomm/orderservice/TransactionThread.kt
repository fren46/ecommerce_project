package com.ecomm.orderservice

import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
@EnableAsync
class TransactionThread {
    @Async
    @Scheduled(fixedRate = 10000)
    @Throws(InterruptedException::class)
    fun scheduleFixedRateTaskAsync() {
        println("Fixed rate task async - " + System.currentTimeMillis() / 1000)
        Thread.sleep(1000)
    }
}