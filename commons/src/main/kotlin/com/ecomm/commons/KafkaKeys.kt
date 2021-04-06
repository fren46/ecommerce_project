package com.ecomm.commons

enum class KafkaKeys(val value: String) {
    KEY_ORDER_CREATED("ordercreated"),
    KEY_ORDER_PAID("orderpaid"),
    KEY_ORDER_CANCELED("ordercanceled"),
    KEY_ORDER_AVAILABLE("orderavailable"),
    KEY_ORDER_FAILED("orderfailed"),
    KEY_ORDER_DELIVERING("orderdelivering"),
    KEY_ORDER_DELIVERED("orderdelivered"),
    KEY_ORDER_CANCELING("ordercanceling"),
    KEY_ORDER_REFUNDED("orderrefunded"),
    KEY_ORDER_RESTOCKED("orderrestocked"),
    KEY_ORDER_CANCELFAILED("ordercancelfailed"),
    KEY_PRODUCT_WARNING("productwarning")

}
