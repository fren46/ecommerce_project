package com.ecomm.warehouseservice.controller

import com.ecomm.commons.SimpleWarehouseDTO
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

    @GetMapping("/warehouses")
    @ApiOperation(value = "Return the list of warehouses")
    fun getWarehouseList(): ResponseEntity<List<WarehouseDTO>?> {
        val warehouseList= warehouseService.getWarehouseList()
        return if (warehouseList != null)
            ResponseEntity(warehouseList, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/warehouses/simple")
    @ApiOperation(value = "Return the list of warehouses")
    fun getSimpleWarehouseList(): ResponseEntity<List<SimpleWarehouseDTO>?> {
        val warehouseList= warehouseService.getSimpleWarehouseList()
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

    @PostMapping("/warning/{warehouseId}/{prodId}")
    fun modifyWarningInWarehouse(@RequestBody t: String,@PathVariable warehouseId: String, @PathVariable prodId: String): ResponseEntity<String> {
        return try {
            val num = t.toInt()
            if(warehouseService.setWarning(warehouseId, prodId, num))
                ResponseEntity("Product alarm modified", HttpStatus.OK)
            else
                ResponseEntity("Product alarm not modified", HttpStatus.NOT_MODIFIED)
        }catch (ex: NumberFormatException) {
            ResponseEntity("Product alarm must be an Integer", HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }

    }
}
