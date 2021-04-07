package com.ecomm.catalogservice.controller

import com.ecomm.commons.WarningProductDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.ProductNotFoundException
import com.ecomm.catalogservice.exception.WarehouseNotFoundException
import com.ecomm.catalogservice.repo.ProductRepository
import com.ecomm.catalogservice.repo.UserRepository
import com.ecomm.commons.SimpleWarehouseDTO
import com.ecomm.commons.WarehouseItem
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.net.URI

@RestController
@RequestMapping("/warehouses")
class WarehouseController (
    private val productRepository: ProductRepository
        ) {

    @Value("\${application.urlWarehouseService}")
    private lateinit var HostWarehouseS: String
    val restTemplate = RestTemplate()

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns all the warehouses")
    fun getWarehouses(): List<SimpleWarehouseDTO>{
        val respType = object: ParameterizedTypeReference<List<SimpleWarehouseDTO>>(){}
        val res = restTemplate.exchange(
            RequestEntity<Any>(HttpMethod.GET, URI.create("http://${HostWarehouseS}/warehouses/simple")),
            respType
        )
        return res.body!!
    }

    @PostMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add product quantity to warehouse")
    fun addProductQuantity(
        @PathVariable
        @ApiParam(value = "warehouse id", required = true)
        id: String,
        @RequestBody
        @ApiParam(value = "WarehouseItem with product id and quantity", required = true)
        warehouseItem: WarehouseItem
    ): Int{
        if (warehouseItem.quantity <= 0){
            throw BadRequestException("Quantity must be greater than zero")
        }
        try {
            val product = productRepository.findById(warehouseItem.productId)
            if (product.isPresent){
                val res = restTemplate.exchange(
                    RequestEntity<Any>(warehouseItem, HttpMethod.POST, URI.create("http://${HostWarehouseS}/product/${id}")),
                    Int::class.java
                )
                if (res.statusCode == HttpStatus.NOT_FOUND)
                    throw WarehouseNotFoundException("Warehouse with id ${id} not found")
                else
                    return res.body!!
            }else{
                throw ProductNotFoundException("Product with id ${warehouseItem.productId} not found")
            }
        }catch (ex: RestClientException){
            throw WarehouseNotFoundException("Warehouse  not found")
        }

    }

    @PostMapping("/{id}/warning")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add warning to a product of the warehouse by id")
    fun addWarningProduct(
        @PathVariable
        @ApiParam(value = "warehouse id", required = true)
        id: String,
        @RequestBody
        @ApiParam(value = "Object that contain product id and warning level", required = true)
        warningProductDTO: WarningProductDTO
    ): String{
        try {
            val res = restTemplate.exchange(
            RequestEntity<Any>(warningProductDTO, HttpMethod.POST, URI.create("http://${HostWarehouseS}/warning/${id}")),
            String::class.java
        )
        val body = res.body
        if (res.statusCode == HttpStatus.OK && body != null)
            return body
        else
            throw BadRequestException("Bad request")
        }catch (ex: RestClientException){
            throw BadRequestException("Bad request")
        }
    }

}