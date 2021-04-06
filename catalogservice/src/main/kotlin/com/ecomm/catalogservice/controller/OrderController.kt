package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.OrderDTO
import com.ecomm.catalogservice.dto.clientOrderDTO
import com.ecomm.catalogservice.exception.*
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.catalogservice.security.CustomUserDetails
import com.ecomm.catalogservice.service.ProductServiceImpl
import com.ecomm.commons.KafkaChannels
import com.ecomm.commons.KafkaKeys
import com.ecomm.commons.OrderStatus
import com.ecomm.commons.UserRole
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.jaxb.SpringDataJaxb
import org.springframework.http.*
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("/orders")
class OrderController(
    private val productService: ProductServiceImpl,
    private val userRepository: UserRepository
    ) {

    @Value("\${application.urlOrderService}")
    private lateinit var HostOrderS: String
    val mapper: ObjectMapper = ObjectMapper()
    val restTemplate = RestTemplate()
    private val TOPIC: String = "status"
    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>

    @GetMapping("")
    @RolesAllowed("ADMIN")
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
    fun addOrder(
        @RequestBody
        @ApiParam(value = "Order object", required = true)
        order: clientOrderDTO?
    ): OrderDTO{
        val auth = SecurityContextHolder.getContext().authentication
        val userDetail = auth.principal as CustomUserDetails
        val isAdmin: (GrantedAuthority) -> Boolean = { it.authority == UserRole.ROLE_ADMIN.toString() }
        val isCustomer: (GrantedAuthority) -> Boolean = { it.authority == UserRole.ROLE_CUSTOMER.toString() }
        var priceList:MutableMap<String, Float> = mutableMapOf<String, Float>()
        var sum: Float = 0.0f
        if ( (order?.buyer == null && userDetail.authorities.any(isAdmin)) || order?.prodList == null ){
            throw BadRequestException("No correct Order in the request")
        }
        if(order?.buyer == null && userDetail.authorities.any(isCustomer)) {
            // if the body of the request don't contain the buyer and the logged user is a Custom
            // the buyer value is filled with the id of the logged User retrieved by the Security Context
            order.buyer = userDetail.id
        }
        if (auth.authorities.any(isAdmin) || (userDetail.id == order.buyer && auth.authorities.any(isCustomer))){
            // calculate prodPrice
            order.prodList.keys.forEach { prodId -> priceList[prodId] = productService.getProductPrice(prodId) }
            // calculate amount
            order.prodList.keys.forEach { prodId -> sum += (order.prodList[prodId]!! * priceList[prodId]!!) }
            val orderDto = OrderDTO(
                buyer = order.buyer,
                prodList = order.prodList,
                prodPrice = priceList,
                amount = sum,
                status = OrderStatus.Pending.toString())
            val endpoint = URI.create("http://${HostOrderS}/orders")
            val request = RequestEntity<OrderDTO>(orderDto, HttpMethod.POST, endpoint)
            val response = restTemplate.exchange(request, OrderDTO::class.java)
            // TODO: 3/30/2021 check for the status code response.statusCode
            val body = response.body
            if (body!= null)
                return body
            else
                throw BadRequestException("Bad Request")
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User ${auth.name} cannot make an order for another user")
        }
    }

    @PostMapping("/{id}/status")
    @RolesAllowed("ADMIN")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Modify order status")
    fun modifyOrderStatus(
        @PathVariable
        @ApiParam(value = "Order id", required = true)
        id: String,
        @RequestBody
        @ApiParam(value = "New Order status", required = true)
        string: String?
    ): OrderDTO{
        if (string == null ){
            throw BadRequestException("New status missing")
        }else if (string == OrderStatus.Delivering.toString() ||
            string == OrderStatus.Delivered.toString()){

            val newOrderStatus = OrderDTO(id=id, status = string)
            val res = restTemplate.exchange(
                RequestEntity<Any>(newOrderStatus, HttpMethod.PUT, URI.create("http://${HostOrderS}/orders")),
                OrderDTO::class.java
            )
            val newOrder = res.body
            if (res.statusCode == HttpStatus.OK && newOrder != null) {
                if (newOrder.status == string){
                    /*if (string == OrderStatus.Delivering.toString())
                        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_DELIVERING.value, newOrder)
                    else if (string != OrderStatus.Delivered.toString())
                        this.kafkaTemplate.send(KafkaChannels.TOPIC.value, KafkaKeys.KEY_ORDER_DELIVERED.value, newOrder)
                    */
                    return newOrder

                }else{
                    throw NewStatusOrderException("Temporarily not able to change the status to ${string}")
                }
            }else{
                // TODO: 4/2/2021 check the statusCode and return the correct error
                throw OrderNotFoundException("Order with id ${id} not found")
            }
        }else{
            throw BadRequestException("New status not allowed")
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete order by id")
    fun deleteProduct(
        @PathVariable
        @ApiParam(value="Order id")
        id: String
    ): OrderDTO{
        val auth = SecurityContextHolder.getContext().authentication
        val userDetail = auth.principal as CustomUserDetails
        //println(userDetail.id)
        //val user = userRepository.findByEmail(auth.name)
        val newOrderStatus = OrderDTO(id=id, status = OrderStatus.Canceled.toString())
        try {
            val res = restTemplate.exchange(
                RequestEntity<Any>(newOrderStatus, HttpMethod.DELETE, URI.create("http://${HostOrderS}/orders/${id}?userId=${userDetail.id}")),
                OrderDTO::class.java
            )
            val body = res.body
            if (res.statusCode == HttpStatus.OK && body != null) {
                if (body.status == OrderStatus.Canceled.toString() )
                    return body
                else
                    throw NewStatusOrderException("Temporarily not able to delete the order ${id}")
            }else{
                // TODO: 4/2/2021 check the statusCode and return the correct error
                throw OrderNotFoundException("Order with id ${id} not found")
            }
        }catch (ex: RestClientException){
            throw OrderNotFoundException("Order with id ${id} not found")
        }

/*
        val res = restTemplate.exchange(
            RequestEntity<Any>(id, HttpMethod.DELETE, URI.create("http://${HostOrderS}/orders/${id}")),
            OrderDTO::class.java
        )
        val body = res.body
        if (body != null){
            if(body.status == OrderStatus.Pending.toString() || body.status == OrderStatus.Issued.toString()){
                this.kafkaTemplate.send(TOPIC, KafkaKeys.KEY_ORDER_CANCELED.value, id)
                return body
            }else{
                throw BadRequestDeletionOrderException("Order with id ${id} has a status \"${body.status}\"." +
                        " It is not possible to delete")
            }
        }else{
            throw OrderNotFoundException("Order with id ${id} not deleted because not found")
        }

 */
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