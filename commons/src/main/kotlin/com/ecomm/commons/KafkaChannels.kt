package com.ecomm.commons

enum class KafkaChannels(val value: String) {
    TOPIC("status"),
    ORDER("com.ecomm.orderservice"),
    WALLET("com.ecomm.walletservice"),
    CATALOG("com.ecomm.catalogservice"),
    WAREHOUSE("com.ecomm.warehouseservice")
}