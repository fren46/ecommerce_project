package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.OrderDTO
import com.ecomm.catalogservice.exception.BadRequestDeletionOrderException
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.OrderListNotFoundException
import com.ecomm.catalogservice.exception.OrderNotFoundException
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.OrderStatus
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI

@RestController
@RequestMapping("/orders")
class OrderController {

    @Value("\${application.urlOrderService}")
    private lateinit var HostOrderS: String
    val mapper: ObjectMapper = ObjectMapper()
    val restTemplate = RestTemplate()
    private val TOPIC: String = "status"
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns all the orders")
    fun getOrders(): List<OrderDTO>{
        //val res = restTemplate.getForObject("http://${HostOrderS}/orders", List::class.java)
        val endpoint = URI.create("http://${HostOrderS}/orders")
        val request = RequestEntity<Any>(HttpMethod.GET, endpoint)
        // solution to problem of List<OrderDTO>. https://stackoverflow.com/questions/39679180/kotlin-call-java-method-with-classt-argument
        val respType = object: ParameterizedTypeReference<List<OrderDTO>>(){}
        val response = restTemplate.exchange(request, respType)
        // TODO: 3/30/2021 check for the status code response.statusCode
        val body = response.body

        if (body != null)
            return body
        else
            throw OrderListNotFoundException("List of order not found")
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns the Order by id")
    fun getOrder(
        @PathVariable
        @ApiParam(value = "Order id", required = true)
        id: String
    ): OrderDTO{
        //val res = restTemplate.getForObject("http://${HostOrderS}/orders/${id}", OrderDTO::class.java)
        val endpoint = URI.create("http://${HostOrderS}/orders/${id}")
        val request = RequestEntity<Any>(HttpMethod.GET, endpoint)
        val response = restTemplate.exchange(request, OrderDTO::class.java)
        // TODO: 3/30/2021 check for the status code response.statusCode
        val body = response.body
        if (body!=null)
            return body
        else
            throw OrderNotFoundException("Order with id ${id} not found")
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add order")
    fun addProduct(
        @RequestBody
        @ApiParam(value = "Order object", required = true)
        order: OrderDTO?
    ): OrderDTO{
        //val res = restTemplate.postForObject("http://${HostOrderS}/orders", order,  OrderDTO::class.java)
        val endpoint = URI.create("http://${HostOrderS}/orders")
        val request = RequestEntity<OrderDTO>(order, HttpMethod.POST, endpoint)
        val response = restTemplate.exchange(request, OrderDTO::class.java)
        // TODO: 3/30/2021 check for the status code response.statusCode
        val body = response.body
        if (body!= null)
            return body
        else
            throw BadRequestException("Bad Request")
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete order by id")
    fun deleteProduct(
        @PathVariable
        @ApiParam(value="Order id")
        id: String
    ): OrderDTO{
        val res = restTemplate.exchange(
            RequestEntity<Any>(HttpMethod.GET, URI.create("http://${HostOrderS}/orders/${id}")),
            OrderDTO::class.java
        )
        // TODO: 3/30/2021 check for the status code response.statusCode
        val body = res.body
        if (body != null){
            if(body.status == OrderStatus.Pending.toString() || body.status == OrderStatus.Paid.toString()){
                this.kafkaTemplate.send(TOPIC, KafkaKeys.KEY_ORDER_CANCELED.value, id)
                return body
            }else{
                throw BadRequestDeletionOrderException("Order with id ${id} has a status \"${body.status}\"." +
                        " It is not possible to delete")
            }
        }else{
            throw OrderNotFoundException("Order with id ${id} not deleted because not found")
        }
    }

/*

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete order by id")
    fun deleteProduct(
        @PathVariable
        @ApiParam(value="Order id")
        id: String
    ): String{
        val endpoint = URI.create("http://${HostOrderS}/orders/${id}")
        val request = RequestEntity<Any>(HttpMethod.DELETE, endpoint)
        val respType = String::class.java
        val response = restTemplate.exchange(request, respType)
        val body = response.body
        if (body != null)
            return body
        else
            throw OrderNotFoundException("Order with id ${id} not found")
    }

 */


}