package com.ecomm.orderservice.controller

import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.commons.OrderDTO
import com.ecomm.orderservice.service.OrderServiceImpl
import org.bson.types.ObjectId
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderServiceImpl: OrderServiceImpl
) {
    private val mapper = Mappers.getMapper(OrderMapper::class.java);



    @PostMapping
    fun createOrder(@RequestBody orderDto: OrderDTO): ResponseEntity<OrderDTO> {
        val p = orderServiceImpl.createOrder(orderDto);
        return ResponseEntity.ok(mapper.toDto(p));
    }

    @GetMapping()
    fun getOrders(): ResponseEntity<List<OrderDTO>> {

        return ResponseEntity.ok(mapper.toDtos(orderServiceImpl.getOrders()))
    }

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: String, @RequestParam userId: String, @RequestParam isAdmin: Boolean): ResponseEntity<OrderDTO> {
        try {
            val p = orderServiceImpl.getOrder(id, userId, isAdmin);
            return ResponseEntity.ok(p!!)
        }catch (ex: ResponseStatusException){
            throw ResponseStatusException(ex.status)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: String, @RequestParam userId: String): ResponseEntity<OrderDTO> {
        val result = orderServiceImpl.cancelOrder(id, userId)
        return if(result!=null)
            ResponseEntity.ok(result)
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND)

    }

    @PutMapping()
    fun modifyOrder(@RequestBody order: OrderDTO): ResponseEntity<OrderDTO> {
        val res = orderServiceImpl.modifyOrder(order)
        return if(res.isPresent)
            ResponseEntity.ok(mapper.toDto(res.get()))
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

}