package com.ecomm.warehouseservice.controller

import com.ecomm.commons.WarehouseItem
import com.ecomm.warehouseservice.dto.WarehouseDTO
import com.ecomm.warehouseservice.service.WarehouseServiceImpl
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RestController
class WarehouseController (val warehouseService: WarehouseServiceImpl) {

    @GetMapping("/product/{id}/availability")
    @ApiOperation(value = "Return the availability of all the product inside all the warehouses")
    fun getProductAvailability(@PathVariable id: String): ResponseEntity<WarehouseItem> {
        val wItem: WarehouseItem? = warehouseService.getProductAvailability(id)
        return if (wItem != null)
            ResponseEntity(wItem, HttpStatus.OK)
        else
            ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping("/product/{id}/consume/{n}")
    @ApiOperation(value = "Consume n product given its ID")
    fun consumeProduct(@PathVariable id: String, @PathVariable n: Int): ResponseEntity<Map<String,Int>?> {
        val consumed= warehouseService.consumeProduct(id,n)
        return if (consumed != null)
            ResponseEntity(consumed, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/warehouse")
    @ApiOperation(value = "Return the list of warehouses")
    fun getWarehouseList(): ResponseEntity<List<WarehouseDTO>?> {
        val warehouseList= warehouseService.getWarehouseList()
        return if (warehouseList != null)
            ResponseEntity(warehouseList, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @PostMapping("/product/{warehouseId}")
    fun addProductInWarehouse(@RequestBody t: WarehouseItem,@PathVariable warehouseId: String): ResponseEntity<String> {
        val product_added= warehouseService.addProductInWarehouse(warehouseId,t)
        if (product_added==0)
            return ResponseEntity("Product not present", HttpStatus.NOT_FOUND)
        if (product_added==1)
            return ResponseEntity("Product quantity increased", HttpStatus.OK)
        return ResponseEntity("Product added ", HttpStatus.OK)
    }
}
