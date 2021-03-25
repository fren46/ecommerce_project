package com.ecomm.catalogservice.repo

import com.ecomm.commons.Product
import com.ecomm.commons.ProductCategory
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository: MongoRepository<Product, ObjectId> {

    fun getProductByNameAndCategoryAndPrice(name:String, category:ProductCategory, price: Float): Product?
}