package com.ecomm.warehouseservice.service

import com.ecomm.commons.SimpleWarehouseDTO
import com.ecomm.commons.WarehouseItem
import com.ecomm.warehouseservice.dto.WarehouseDTO

interface WarehouseService {
    fun getProductAvailability(id:String):WarehouseItem?
    fun consumeProduct(wh: String, id:String,n:Int):Map<String,Int>?
    fun getWarehouseList():List<WarehouseDTO>?
    fun getSimpleWarehouseList():List<SimpleWarehouseDTO>?
    fun addProductInWarehouse(warehouseID:String, item: WarehouseItem):Int?
}