package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.service.ProductMapper
import com.ecomm.catalogservice.service.ProductService
import org.bson.types.ObjectId
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/products")
class ProductController (
    private val productService: ProductService
    ){

    private val mapper = Mappers.getMapper(ProductMapper::class.java);

    @GetMapping("/")
    fun getProducts():List<ProductDTO>{
        return productService.getProducts().map { product -> mapper.toDto(product) }
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: ObjectId): ProductDTO{
        val p = productService.getProduct(id)
        if(p.isPresent)
            return mapper.toDto(p.get())
        else
            throw ResponseStatusException(HttpStatus.NOT_FOUND);
    }



//    per comunicare con gli altri service
//    val restTemplate = RestTemplate()
//    val res = restTemplate.getForObject("http://${host}/square?v=${v}", Int::class.java)

}