package com.ecomm.orderservice.controller

import com.ecomm.orderservice.dto.OrderMapper
import com.ecomm.commons.OrderDTO
import com.ecomm.orderservice.service.OrderServiceImpl
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
    fun getOrder(@PathVariable id: String): ResponseEntity<OrderDTO> {
        val p = orderServiceImpl.getOrder(id);
        return if(p.isPresent)
            ResponseEntity.ok(mapper.toDto(p.get()))
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: String): ResponseEntity<String> {
        val result = orderServiceImpl.cancelOrder(id)
        return if(result)
            ResponseEntity.ok(id)
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
    /*@GetMapping("/open/{id}")
    fun getProductOpen(@PathVariable id: ObjectId): ResponseEntity<OrderDTO> {
        val p = orderServiceImpl.getOrder(id)
        if(p.isPresent)
            return ResponseEntity.ok(mapper.toDto(p.get()))
        else
            return ResponseEntity.notFound().build()
    }

    @GetMapping("/byrole/{id}")
    fun getProductByRole(@PathVariable id: ObjectId): OrderDTO{
        val p = orderServiceImpl.getOrder(id)
        if(p.isPresent)
            return mapper.toDto(p.get())
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    */
    // example in case products had an owner
    // see https://www.baeldung.com/spring-security-method-security
    //@PreAuthorize("#owner == authentication.principal.username")
    //@GetMapping("/byownw/{id}")
    //fun getProductByOwnner(@PathVariable owner: String): ProductDTO {
    //
    //}
    // https://www.baeldung.com/get-user-in-spring-security


}