package com.ecomm.commons

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Document("orders")
class Order(
    @Id var id: String? = ObjectId.get().toString(),
    @CreatedDate var createdDate: LocalDateTime? = null,
    @LastModifiedDate var modifiedDate: LocalDateTime? = null,
    var prodList: MutableMap<String, Int> = mutableMapOf<String, Int>(),
    var wHRecord: MutableMap<String, MutableMap<String, Int>> = mutableMapOf<String, MutableMap<String, Int>>(),
    var buyer: String? = null,
    var prodPrice: MutableMap<String, Float> = mutableMapOf<String, Float>(),
    var amount: Float? = null,
    var status: OrderStatus = OrderStatus.Pending
) {
    override fun toString(): String {
        return "{ID: " + this.id + ", created: " + this.createdDate +
        ", modified: " + this.modifiedDate + ", buyer: " + this.buyer +
                ", amount: " + this.amount + ", status: " + this.status + "}"
    }
}