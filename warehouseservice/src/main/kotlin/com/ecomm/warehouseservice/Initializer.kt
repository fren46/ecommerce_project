package com.ecomm.warehouseservice
import com.ecomm.commons.Warehouse
import com.ecomm.commons.WarehouseItem
import com.ecomm.warehouseservice.repository.WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer:CommandLineRunner {

    @Autowired
    lateinit var repo:WarehouseRepository

    override fun run(vararg args: String?) {
        repo.deleteAll()
        val prod1= WarehouseItem("fifn",3)
        val prod2= WarehouseItem("ciao",3)
        val prod3= WarehouseItem("bello",3)
        val warehouse1= Warehouse(name= "Milan",stocks = mutableSetOf(prod1,prod2))
        val warehouse2= Warehouse(name= "Turin",stocks = mutableSetOf(prod1,prod2,prod3))
        repo.save(warehouse1)
        repo.save(warehouse2)



    }
}