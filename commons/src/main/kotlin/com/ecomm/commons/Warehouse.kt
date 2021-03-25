package com.ecomm.commons

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Warehouse(
    @Id
    var name: String?=null,
    var stocks:  MutableSet<WarehouseItem> = mutableSetOf()
)