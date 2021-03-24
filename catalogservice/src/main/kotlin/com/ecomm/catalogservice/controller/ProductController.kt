package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.service.ProductMapper
import com.ecomm.catalogservice.service.ProductService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.bson.types.ObjectId
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/products")
class ProductController (
    private val productService: ProductService
    ){

    private val mapper = Mappers.getMapper(ProductMapper::class.java);

    @GetMapping("")
    @ApiOperation(value = "Returns all the products")
    fun getProducts():List<ProductDTO>{
        return productService.getProducts().map { product -> mapper.toDto(product) }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Returns the Product by id")
    fun getProduct(
        @PathVariable
        @ApiParam(value = "Product id", required = true)
        id: ObjectId
    ): ProductDTO{
        val p = productService.getProduct(id)
        if(p.isPresent)
            return mapper.toDto(p.get())
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/test")
    fun produceEventTest(@RequestBody product: ProductDTO){
        productService.produceTestEvent(product)
    }



//    per comunicare con gli altri service
//    val restTemplate = RestTemplate()
//    val res = restTemplate.getForObject("http://${host}/square?v=${v}", Int::class.java)

}