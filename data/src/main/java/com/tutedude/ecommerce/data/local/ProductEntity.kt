package com.tutedude.ecommerce.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val price: Double,
    val images: List<String> = emptyList(),
    val uploaderUid: String? = null,
    val category: String? = null,
    val isFavorite: Boolean = false
)
