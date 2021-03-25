package com.ecomm.commons
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Warehouse(
    @Id
    val id: String = ObjectId.get().toHexString(),
    var name: String?=null,
    var stocks:  MutableSet<WarehouseItem> = mutableSetOf()
)