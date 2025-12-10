package com.tutedude.ecommerce.data.remote

import retrofit2.http.GET

data class FakeStoreProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String
)

interface FakeStoreApi {
    @GET("products")
    suspend fun getProducts(): List<FakeStoreProductDto>
}
