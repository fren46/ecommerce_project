package com.ecomm.catalogservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CatalogserviceApplication(
    @Value("#{application.urlOrderService}") private var HostOrderS: String,
    @Value("#{application.urlWalletService}") private var HostWalletS: String,
    @Value("#{application.urlWarehouseService}") private var HostWarehouseS: String)

fun main(args: Array<String>) {
    runApplication<CatalogserviceApplication>(*args)
}
