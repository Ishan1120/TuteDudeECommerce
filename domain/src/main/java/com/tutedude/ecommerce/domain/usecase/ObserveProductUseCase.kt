package com.tutedude.ecommerce.domain.usecase

import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ObserveProductUseCase(private val repo: ProductRepository) {
    operator fun invoke(id: Long): Flow<Product?> = repo.observeProduct(id)
}
