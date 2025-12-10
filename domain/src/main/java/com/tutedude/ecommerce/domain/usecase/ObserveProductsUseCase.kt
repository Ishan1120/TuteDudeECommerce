package com.tutedude.ecommerce.domain.usecase

import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ObserveProductsUseCase(private val repo: ProductRepository) {
    operator fun invoke(): Flow<List<Product>> = repo.observeProducts()
}
