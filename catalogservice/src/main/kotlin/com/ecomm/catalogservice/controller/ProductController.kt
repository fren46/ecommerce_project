package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.ProductNotFoundException
import com.ecomm.catalogservice.service.ProductMapper
import com.ecomm.catalogservice.service.ProductServiceImpl
import com.ecomm.commons.WarehouseItem
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.net.URI

@RestController
@RequestMapping("/products")
class ProductController (
    private val productService: ProductServiceImpl
    ){

    @Value("\${application.urlWarehouseService}")
    private lateinit var HostWarehouseS: String
    private val mapper = Mappers.getMapper(ProductMapper::class.java)
    val restTemplate = RestTemplate()
    @Autowired
    lateinit var cacheManager: CacheManager

    fun evictAllCacheValues(cacheName: String) {
        val cache = cacheManager.getCache(cacheName)
        if (cache != null) {
            cache.invalidate()
        }else {
            print("cache not found")
        }
    }

    @Scheduled(fixedRate = 60000)
    fun evictAllCacheValuesAtIntervals() {
        evictAllCacheValues("warehouseItem")
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns all the products")
    fun getProducts():List<ProductDTO>{
        return productService.getProducts().map { product -> mapper.toDto(product) }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Returns the Product by id")
    fun getProduct(
        @PathVariable
        @ApiParam(value = "Product id", required = true)
        id: String
    ): ProductDTO{
        val p = productService.getProduct(id)
        if(p.isPresent)
            return mapper.toDto(p.get())
        else
            throw ProductNotFoundException("Product with id ${id} not found")
    }

    @GetMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.OK)
    @Cacheable(value = ["warehouseItem"])
    @ApiOperation(value = "Returns the availability of Product by id")
    fun getAvailabilityProduct(
        @PathVariable
        @ApiParam(value = "Product id", required = true)
        id: String
    ): MutableMap<String,Any>{
        val res = restTemplate.exchange(
            RequestEntity<Any>(HttpMethod.GET, URI.create("http://${HostWarehouseS}/product/${id}/availability")),
            WarehouseItem::class.java
        )
        val body = res.body
        if (res.statusCode == HttpStatus.OK && body != null) {
            return mutableMapOf("productId" to body.productId, "quantity" to body.quantity)
        }else{
            // TODO: 4/2/2021 check the statusCode and return the correct error
            throw ProductNotFoundException("Product with id ${id} not found")
        }
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add product to catalog")
    fun addProduct(
        @RequestBody
        @ApiParam(value = "Product object", required = true)
        product: ProductDTO?
    ): ProductDTO{
        if (product == null) {
            throw BadRequestException("Request without product")
        } else if (product.name == null || product.category == null || product.price == null){
            throw BadRequestException("Name, Category and Price of the product must not be null")
        }
        val prod = productService.addProduct(product)
        return mapper.toDto(prod)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Modify product by id")
    fun modifyProduct(
        @PathVariable
        @ApiParam(value = "Product id", required = true)
        id: String,
        @RequestBody
        @ApiParam(value="Product Object", required = true)
        product: ProductDTO?
    ): ProductDTO{
        if (product == null)
            throw BadRequestException("Request without product")
        val prod = productService.modifyProduct(id, product)
        if (prod.isPresent)
            return mapper.toDto(prod.get())
        else
            throw ProductNotFoundException("Product with id ${id} not found")
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete product by id")
    fun deleteProduct(
        @PathVariable
        @ApiParam(value="Product id")
        id: String
    ): String{
        val p = productService.deleteProduct(id)
        if ( p.isPresent )
            return "Product with id ${id} deleted successfully"
        else
            throw ProductNotFoundException("Product with id ${id} not found")
    }

}