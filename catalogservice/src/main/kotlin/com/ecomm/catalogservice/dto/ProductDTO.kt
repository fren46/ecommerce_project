package com.ecomm.catalogservice.dto

import com.ecomm.commons.ProductCategory

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