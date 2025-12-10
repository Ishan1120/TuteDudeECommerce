package com.tutedude.ecommerce.data.recommendation

import com.tutedude.ecommerce.data.remote.FakeStoreApi
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.RecommendationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepositoryImpl @Inject constructor(
    private val api: FakeStoreApi
) : RecommendationRepository {
    override suspend fun fetchRecommended(): List<Product> =
        api.getProducts().map { dto ->
            Product(
                id = 1_000_000L + dto.id,
                title = dto.title,
                description = dto.description,
                price = dto.price,
                images = listOf(dto.image),
                uploaderUid = null,
                category = dto.category
            )
        }
}
