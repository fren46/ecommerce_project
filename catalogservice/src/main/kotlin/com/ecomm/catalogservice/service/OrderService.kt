package com.ecomm.catalogservice.service

import com.ecomm.commons.OrderDTO

interface OrderService {
    fun notifyClient(order: OrderDTO, str: String)
}