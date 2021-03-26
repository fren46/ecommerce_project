package com.ecomm.catalogservice.dto

import com.ecomm.commons.ProductCategory
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Field
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

// assumiamo che name, category e price siano obbligatori
// perchè poi in base a quelli controlliamo anche se sul db
// un prodotto c'è già
data class ProductDTO(
    var id: String? = null,
    var name: String?,
    var description: String? = null,
    var picture: String? = null,
    var category: ProductCategory?,
    var price: Float?
)