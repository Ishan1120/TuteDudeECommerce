package com.tutedude.ecommerce.domain.model

data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val images: List<String> = emptyList(),
    val uploaderUid: String? = null,
    val category: String? = null
)
