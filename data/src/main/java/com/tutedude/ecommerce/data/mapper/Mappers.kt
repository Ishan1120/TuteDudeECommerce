package com.tutedude.ecommerce.data.mapper

import com.tutedude.ecommerce.data.local.ProductEntity
import com.tutedude.ecommerce.domain.model.Product

fun ProductEntity.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    images = images,
    uploaderUid = uploaderUid,
    category = category
)

fun Product.toEntity(isFavorite: Boolean = false) = ProductEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    images = images,
    uploaderUid = uploaderUid,
    category = category,
    isFavorite = isFavorite
)
