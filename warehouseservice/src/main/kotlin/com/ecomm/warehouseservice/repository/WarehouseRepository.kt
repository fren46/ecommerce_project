package com.ecomm.warehouseservice.repository
import com.ecomm.commons.Transaction
import com.ecomm.commons.Warehouse
import org.springframework.data.mongodb.repository.MongoRepository

interface WarehouseRepository: MongoRepository<Warehouse, String> {
     //fun getWarehouseById(id:String):Warehouse
     //fun getStocksById(id:String):Map<String,Int>?
}