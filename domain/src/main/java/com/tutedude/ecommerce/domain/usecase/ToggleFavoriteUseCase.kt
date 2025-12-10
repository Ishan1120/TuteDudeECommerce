package com.tutedude.ecommerce.domain.usecase

import com.tutedude.ecommerce.domain.repository.ProductRepository

class ToggleFavoriteUseCase(private val repo: ProductRepository) {
    suspend operator fun invoke(id: Long) = repo.toggleFavorite(id)
}
