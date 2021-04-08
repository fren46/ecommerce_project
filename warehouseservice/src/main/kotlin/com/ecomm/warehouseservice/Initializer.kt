package com.ecomm.warehouseservice
import com.ecomm.commons.Warehouse
import com.ecomm.warehouseservice.repository.WarehouseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Initializer:CommandLineRunner {

    @Autowired
    lateinit var repo:WarehouseRepository

    override fun run(vararg args: String?) {
        if (repo.findAll().isEmpty()){
            val warehouse1= Warehouse(name= "Milan",stocks = mutableSetOf())
            val warehouse2= Warehouse(name= "Turin",stocks = mutableSetOf())
            repo.save(warehouse1)
            repo.save(warehouse2)
        }
    }
}