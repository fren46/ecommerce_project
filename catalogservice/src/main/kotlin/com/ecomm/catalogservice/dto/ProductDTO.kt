package com.ecomm.catalogservice.dto

import com.ecomm.commons.ProductCategory
import org.bson.types.ObjectId
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

// assumiamo che name, category e price siano obbligatori
// perchè poi in base a quelli controlliamo anche se sul db
// un prodotto c'è già
data class ProductDTO(
    var id: ObjectId? = null,
    @NotBlank(message = "Name is mandatory")
    var name: String,
    var description: String? = null,
    var picture: String? = null,
    @NotBlank(message = "Category is mandatory")
    var category: ProductCategory,
    @NotBlank(message = "Price is mandatory")
    var price: Float
)