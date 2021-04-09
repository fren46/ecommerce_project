package com.ecomm.warehouseservice.controller

import com.ecomm.commons.SimpleWarehouseDTO
import com.ecomm.commons.WarehouseItem
import com.ecomm.commons.WarningProductDTO
import com.ecomm.warehouseservice.dto.WarehouseDTO
import com.ecomm.warehouseservice.service.WarehouseServiceImpl
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping("/{wh}/product/{id}/consume")
    @ApiOperation(value = "Consume n product given its ID")
    fun consumeProduct(@PathVariable wh: String,@PathVariable id: String, @RequestParam quantity: Int): ResponseEntity<Map<String,Int>?> {
        val consumed= warehouseService.consumeProduct(wh,id,quantity)
        return if (consumed != null)
            ResponseEntity(consumed, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/warehouses")
    @ApiOperation(value = "Return the list of warehouses")
    fun getWarehouseList(): ResponseEntity<List<WarehouseDTO>?> {
        val warehouseList= warehouseService.getWarehouseList()
        return if (warehouseList.isNotEmpty())
            ResponseEntity(warehouseList, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/warehouses/simple")
    @ApiOperation(value = "Return the list of simple warehouses")
    fun getSimpleWarehouseList(): ResponseEntity<List<SimpleWarehouseDTO>?> {
        val warehouseList= warehouseService.getSimpleWarehouseList()
        return ResponseEntity(warehouseList, HttpStatus.OK)
    }

    @PostMapping("/product/{warehouseId}")
    fun addProductInWarehouse(@RequestBody t: WarehouseItem, @PathVariable warehouseId: String): ResponseEntity<Int> {
        val quantity = warehouseService.addProductInWarehouse(warehouseId,t)
        if (quantity == null)
            return ResponseEntity(HttpStatus.NOT_FOUND)
        else
            return ResponseEntity(quantity, HttpStatus.OK)
    }

    @DeleteMapping("/product/{productId}")
    fun deleteProducts(@PathVariable productId: String): ResponseEntity<String> {
        return ResponseEntity(warehouseService.deleteProductAll(productId),HttpStatus.OK)
    }

    @PostMapping("/warning/{warehouseId}")
    fun modifyWarningInWarehouse(@RequestBody t: WarningProductDTO,@PathVariable warehouseId: String): ResponseEntity<String> {
        return if(warehouseService.setWarning(warehouseId, t.productId, t.warning))
            ResponseEntity("Product alarm modified", HttpStatus.OK)
        else
            ResponseEntity("Product alarm not modified", HttpStatus.NOT_MODIFIED)

    }
}
