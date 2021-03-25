package com.ecomm.commons

enum class KafkaKeys(val value: String) {
    KEY_ORDER_CREATED("ordercreated"),
    KEY_ORDER_PAID("orderpaid"),
    KEY_ORDER_CANCELED("ordercanceled"),
    KEY_ORDER_AVAILABLE("orderavailable"),
    KEY_ORDER_FAILED("orderfailed")
}
