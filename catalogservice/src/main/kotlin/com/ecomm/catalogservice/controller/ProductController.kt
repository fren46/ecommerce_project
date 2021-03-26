package com.ecomm.catalogservice.controller

import com.ecomm.catalogservice.dto.ProductDTO
import com.ecomm.catalogservice.exception.BadRequestException
import com.ecomm.catalogservice.exception.ProductNotFoundException
import com.ecomm.catalogservice.service.ProductMapper
import com.ecomm.catalogservice.service.ProductServiceImpl
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.bson.types.ObjectId
import org.mapstruct.factory.Mappers
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.lang.Error
import java.lang.IllegalArgumentException
import javax.validation.Valid

@RestController
@RequestMapping("/products")
class ProductController (
    private val productService: ProductServiceImpl
    ){

    private val mapper = Mappers.getMapper(ProductMapper::class.java);

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

    @PostMapping("/test")
    fun produceEventTest(@RequestBody product: ProductDTO){
        productService.produceTestEvent(product)
    }



//    per comunicare con gli altri service
//    val restTemplate = RestTemplate()
//    val res = restTemplate.getForObject("http://${host}/square?v=${v}", Int::class.java)

}