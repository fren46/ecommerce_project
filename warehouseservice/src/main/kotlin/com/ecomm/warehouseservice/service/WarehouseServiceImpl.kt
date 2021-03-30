package com.ecomm.warehouseservice.service
import com.ecomm.commons.WarehouseItem
import com.ecomm.warehouseservice.dto.WarehouseDTO
import com.ecomm.warehouseservice.dto.WarehouseMapper
import com.ecomm.warehouseservice.repository.WarehouseRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class WarehouseServiceImpl(private val repo:WarehouseRepository): WarehouseService {
    private val mapper = Mappers.getMapper(WarehouseMapper::class.java)
    /* TODO Aggiungere Kafka Listener con group_id settato a "warehouse" */

    override fun getProductAvailability(id:String):WarehouseItem? {
        val warehouseList = repo.findAll()
        val productList = mutableListOf<WarehouseItem>()
        warehouseList.forEach { warehouse -> warehouse.stocks.forEach { it -> productList.add(it) } }
        val finallist=productList.groupBy { it.productId }                  // group items by name
            .values                               // take list of values
            .map {                                // for each list
                it.reduce {                       // accumulate counts
                        acc, item ->
                    WarehouseItem(item.productId, acc.quantity + item.quantity)
                }
            }
        finallist.forEach{if(it.productId==id) return it}
        return null
    }
    override fun getWarehouseList():List<WarehouseDTO>?{
        val warehouseList= mutableListOf<WarehouseDTO>()
        repo.findAll().forEach{warehouse -> warehouseList.add(mapper.toDto(warehouse))}
        return warehouseList
    }

    override fun addProductInWarehouse(warehouseID: String, item: WarehouseItem): String {
        val warehouseList = repo.findAll()
        warehouseList.forEach{warehouse -> var count=0; if(warehouse.name==warehouseID) {
            warehouse.stocks.forEach{it->if(it.productId==item.productId) {
                it.quantity+=item.quantity
                repo.save(warehouse)
                return item.productId + " updated"
                }
                count+=1}
        if(count==warehouse.stocks.size){
            warehouse.stocks.add(item)
            repo.save(warehouse)
            return item.productId + " added"
        } } }
        return "Warehouse not present"
    }

    override fun consumeProduct(id: String, n: Int): Map<String,Int>? {
        if(getProductAvailability(id)!!.quantity>=n){
            var quantity=n
            val map= mutableMapOf<String,Int>()
            val warehouseList = repo.findAll()
            warehouseList.forEach{warehouse-> warehouse.stocks.forEach { item ->
                if (item.productId == id) {
                    if (item.quantity >= quantity) {
                        item.quantity -= quantity
                        map.put(warehouse.id, quantity)
                        repo.save(warehouse)
                        return map
                    }
                    if (item.quantity < quantity && item.quantity!=0){
                        map.put(warehouse.id, item.quantity)
                        quantity-=item.quantity
                        item.quantity=0
                        repo.save(warehouse)
                    }
                    }
                }
            }

        }
        return null
    }
}
