package com.ecomm.warehouseservice.controller

import com.ecomm.commons.WarehouseItem
import com.ecomm.warehouseservice.dto.WarehouseDTO
import com.ecomm.warehouseservice.service.WarehouseServiceImpl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException

@RestController
class WarehouseController (val warehouseService: WarehouseServiceImpl) {

    @GetMapping("/product/{id}/availability")
    fun getProductAvailability(@PathVariable id: String): ResponseEntity<WarehouseItem> {
        val wItem: WarehouseItem? = warehouseService.getProductAvailability(id)
        return if (wItem != null)
            ResponseEntity(wItem, HttpStatus.OK)
        else
            ResponseEntity(null, HttpStatus.NOT_FOUND)
    }

    @GetMapping("/product/{id}/consume/{n}")
    fun consumeProduct(@PathVariable id: String, @PathVariable n: Int): Map<String,Int>? {
        return warehouseService.consumeProduct(id,n)
    }

    @GetMapping("/warehouse")
    fun getWarehouseList(): List<WarehouseDTO>? {
        return warehouseService.getWarehouseList()
    }

    @PostMapping("/product/{warehouseId}")
    fun addProductInWarehouse(@RequestBody t: WarehouseItem,@PathVariable warehouseId: String): String {
        return warehouseService.addProductInWarehouse(warehouseId,t)
    }

}
