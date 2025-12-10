package com.tutedude.ecommerce.domain.repository

import com.tutedude.ecommerce.domain.model.Product

interface RecommendationRepository {
    suspend fun fetchRecommended(): List<Product>
}