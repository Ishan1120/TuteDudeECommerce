package com.tutedude.ecommerce.domain.repository

import com.tutedude.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    fun observeProduct(id: Long): Flow<Product?>
    fun observeFavorites(): Flow<List<Product>>
    suspend fun toggleFavorite(id: Long)
    suspend fun ensureSeeded()
    suspend fun refreshFromRemote()
    suspend fun createProduct(title: String, description: String, price: Double, imageUris: List<String>, uploaderUid: String?, category: String?)
}
