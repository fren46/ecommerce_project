package com.ecomm.warehouseservice.dto

import com.ecomm.commons.WarehouseItem

data class WarehouseDTO (
    var id: String? = null,
    var name: String?=null,
    var stocks: MutableSet<WarehouseItem> = mutableSetOf()
)