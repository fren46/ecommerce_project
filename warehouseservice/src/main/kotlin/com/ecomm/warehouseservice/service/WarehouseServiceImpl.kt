package com.ecomm.warehouseservice.service
 import com.ecomm.commons.*
 import com.ecomm.warehouseservice.dto.WarehouseDTO
import com.ecomm.warehouseservice.dto.WarehouseMapper
import com.ecomm.warehouseservice.repository.WarehouseRepository
import org.mapstruct.factory.Mappers
 import org.springframework.beans.factory.annotation.Autowired
 import org.springframework.kafka.annotation.KafkaListener
 import org.springframework.kafka.core.KafkaTemplate
 import org.springframework.kafka.support.KafkaHeaders
 import org.springframework.messaging.handler.annotation.Header
 import org.springframework.messaging.handler.annotation.Payload
 import org.springframework.stereotype.Service
 import org.springframework.transaction.annotation.Transactional
 import java.io.IOException
 import java.time.LocalDateTime

@Service
class WarehouseServiceImpl(private val repo:WarehouseRepository): WarehouseService {
    private val mapper = Mappers.getMapper(WarehouseMapper::class.java)

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, OrderDTO>
    @Autowired
    lateinit var kafkaTemplate2: KafkaTemplate<String, WarningDTO>

    @KafkaListener(topics = ["warning"], groupId = "warehouse")
    @Throws(IOException::class)
    @Transactional
    fun consume2(@Payload dto: WarningDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaKeys.KEY_PRODUCT_WARNING.value) {
            println("PRODUCT WARNING: $dto")
        }
    }

    @KafkaListener(topics = ["status"], groupId = "warehouse")
    @Throws(IOException::class)
    @Transactional
    fun consume(@Payload dto: OrderDTO, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) key: String) {
        if (key == KafkaKeys.KEY_ORDER_PAID.value) {
            println("PAID: $dto")
            return
        }
        else if (key == KafkaKeys.KEY_ORDER_CANCELED.value) {
            reStockProducts(dto.whrecord)
            println("CANCELED: $dto")
            return
        }
        else if (key == KafkaKeys.KEY_ORDER_FAILED.value) {
            reStockProducts(dto.whrecord)
            println("FAILED: $dto")
            return
        }
        else if (key == KafkaKeys.KEY_ORDER_CREATED.value) {
            var temp = dto.copy(prodList = dto.prodList, whrecord = consumeProducts(dto.prodList))

            if (temp.whrecord.isEmpty()) {
                this.kafkaTemplate.send(
                    KafkaChannels.TOPIC.value,
                    KafkaKeys.KEY_ORDER_FAILED.value,
                    temp)

            }
            else {
                this.kafkaTemplate.send(
                    KafkaChannels.TOPIC.value,
                    KafkaKeys.KEY_ORDER_AVAILABLE.value,
                    temp
                )
                temp.whrecord.forEach {wh ->
                    wh.value.forEach {item ->
                        if(isProdAvailabilityLow(item.key, wh.key)) {
                            val whItem = WarningDTO(wh.key, item.key)
                            this.kafkaTemplate2.send(
                                KafkaChannels.WARNING.value,
                                KafkaKeys.KEY_PRODUCT_WARNING.value,
                                whItem
                            )
                        }
                    }
                }
            }
            println("CREATED: $dto")
            return
        }

        else if (key == KafkaKeys.KEY_ORDER_AVAILABLE.value) {
            println("AVAILABLE: $dto")
            return
        }
    }

    fun setWarning(whId: String, prodId: String, warning: Int): Boolean {
        if(warning <= 0)
            return false
        val warehouse = repo.getWarehouseById(whId)
        warehouse?.stocks?.forEach { item ->
            if (item.productId == prodId) {
                item.alarm = warning
                repo.save(warehouse)
                return true
            }
        }
        return false
    }

    override fun getProductAvailability(id:String): WarehouseItem? {
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
        return WarehouseItem(productId = id, quantity = 0)
    }

    fun isProdAvailabilityLow(prodId: String, whId: String): Boolean {
        val warehouse = repo.getWarehouseById(whId)
        if (warehouse != null) {
            warehouse.stocks.forEach {item ->
                if (item.productId == prodId)
                    if(item.quantity <= item.alarm)
                        return true
            }
        }
        return false
    }

    override fun getWarehouseList(): List<WarehouseDTO> {
        val warehouseList= mutableListOf<WarehouseDTO>()
        repo.findAll().forEach{warehouse -> warehouseList.add(mapper.toDto(warehouse))}
        return warehouseList
    }

    override fun getSimpleWarehouseList(): List<SimpleWarehouseDTO> {
        val warehouseList= mutableListOf<SimpleWarehouseDTO>()
        repo.findAll().forEach{warehouse -> warehouseList.add(SimpleWarehouseDTO(id = warehouse.id,name=warehouse.name))}
        return warehouseList
    }

    override fun addProductInWarehouse(warehouseID: String, item: WarehouseItem): Int? {
        val warehouse = repo.findById(warehouseID)
        if (warehouse.isPresent){
            val wh = warehouse.get()
            wh.stocks.forEach{
                if(it.productId==item.productId) {
                    it.quantity+=item.quantity
                    repo.save(wh)
                    return it.quantity
                }
            }
            // if item not found it is added
            wh.stocks.add(item)
            repo.save(wh)
            return item.quantity
        }else
            return null

//        val warehouseList = repo.findAll()
//        warehouseList.forEach {warehouse -> var count=0; if(warehouse.id==warehouseID) {
//            warehouse.stocks.forEach{it->if(it.productId==item.productId) {
//                it.quantity+=item.quantity
//                repo.save(warehouse)
//                return it.quantity
//            }
//                count+=1}
//            if(count==warehouse.stocks.size){
//                warehouse.stocks.add(item)
//                repo.save(warehouse)
//                return 2
//            } } }
//        return 0
    }


    fun reStockProducts(whrecord: MutableMap<String, MutableMap<String,Int>>) {
        if (whrecord.isNullOrEmpty()) {
            return
        }
        var temp = HashMap(whrecord)
        temp.forEach { wh ->
            wh.value.forEach { item ->
                println(addProductInWarehouse(wh.key, WarehouseItem(item.key, item.value)))
            }
        }
        return
    }

    fun consumeProducts(products: MutableMap<String, Int>): MutableMap<String, MutableMap<String, Int>> {

        var whrecord = mutableMapOf<String,MutableMap<String, Int>>()
        var prods = HashMap(products)
        if(products.all { getProductAvailability(it.key)!!.quantity >= it.value }) {

            val warehouseList = repo.findAll()

            warehouseList.forEach{ wh ->

                wh.stocks.forEach{ item ->

                    if (item.productId in prods.keys) {
                        //var quantity = products[item.productId]!!
                        if(prods[item.productId]!! == 0) {
                            println("is 0")
                        }
                        else if (item.quantity >= prods[item.productId]!!) {
                            item.quantity -= prods[item.productId]!!
                            println("$item")

                            whrecord.putIfAbsent(wh.id, mutableMapOf())
                            whrecord[wh.id]?.put(item.productId,prods[item.productId]!!)
                            prods[item.productId] = 0
                            repo.save(wh)
                        }
                        else if ((item.quantity < prods[item.productId]!!).and(item.quantity != 0)){
                            whrecord.putIfAbsent(wh.id, mutableMapOf())
                            whrecord[wh.id]?.put(item.productId,item.quantity)
                            prods[item.productId] = prods[item.productId]!! - item.quantity
                            item.quantity = 0
                            repo.save(wh)
                        }
                    }
                }
            }
        }

        var count = 0
        prods.forEach {prod ->
            count += prod.value
        }

        return if (count == 0)
            whrecord
        else {
            reStockProducts(whrecord)
            whrecord.clear()
            whrecord
        }
    }


    override fun consumeProduct(wh: String, id: String, n: Int): Map<String,Int>? {
        val warehouse = repo.getWarehouseById(wh)
        if (warehouse != null) {
            warehouse.stocks.forEach{ if(it.productId==id && it.quantity>n) {
                it.quantity-=n
                repo.save(warehouse)
                if(it.quantity<=it.alarm)
                    this.kafkaTemplate2.send(
                        KafkaChannels.WARNING.value,
                        KafkaKeys.KEY_PRODUCT_WARNING.value,
                        WarningDTO(wh,id))
                return mapOf(it.productId to it.quantity)
            }
            }
        }
        return null
    }
}
