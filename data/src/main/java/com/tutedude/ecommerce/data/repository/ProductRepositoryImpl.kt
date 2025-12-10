package com.tutedude.ecommerce.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.ListenerRegistration
import com.tutedude.ecommerce.data.local.ProductDao
import com.tutedude.ecommerce.data.local.ProductEntity
import com.tutedude.ecommerce.data.mapper.toDomain
import com.tutedude.ecommerce.data.remote.ProductRemoteDto
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val dao: ProductDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val appContext: Context,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    private var listener: ListenerRegistration? = null

    init {
        // Start realtime sync: mirror Firestore products into Room
        listener = firestore.collection("products").addSnapshotListener { snap, _ ->
            if (snap == null) return@addSnapshotListener
            val list = snap.documents.map { d ->
                val dto = ProductRemoteDto.fromDoc(d)
                ProductEntity(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    price = dto.price,
                    images = dto.images,
                    uploaderUid = dto.uploaderUid,
                    category = dto.category
                )
            }
            // Launch on IO
            kotlinx.coroutines.GlobalScope.launch(io) { dao.upsertAll(list) }
        }
    }

    override fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeProduct(id: Long): Flow<Product?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun observeFavorites(): Flow<List<Product>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun toggleFavorite(id: Long) = withContext(io) {
        dao.toggleFavorite(id)
    }

    override suspend fun ensureSeeded() = withContext(io) {
        if (dao.count() == 0) {
            val seed = listOf(
                ProductEntity(1, "Wireless Headphones", "Bluetooth over-ear", 59.99, category = "Electronics"),
                ProductEntity(2, "Smart Watch", "Fitness tracking", 89.99, category = "Electronics"),
                ProductEntity(3, "Backpack", "Water-resistant", 39.99, category = "Accessories"),
                ProductEntity(4, "Phone Stand", "Adjustable aluminum", 14.99, category = "Accessories")
            )
            dao.upsertAll(seed)
        }
    }

    override suspend fun refreshFromRemote() = withContext(io) {
        val snap = firestore.collection("products").get().await()
        val list = snap.documents.map { d ->
            val dto = ProductRemoteDto.fromDoc(d)
                ProductEntity(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    price = dto.price,
                    images = dto.images,
                    uploaderUid = dto.uploaderUid,
                    category = dto.category
                )
        }
        if (list.isNotEmpty()) dao.upsertAll(list)
    }

    override suspend fun createProduct(
        title: String,
        description: String,
        price: Double,
        imageUris: List<String>,
        uploaderUid: String?,
        category: String?
    ) = withContext(io) {
        val id = System.currentTimeMillis()
        val urls = mutableListOf<String>()
        imageUris.forEachIndexed { index, uriStr ->
            val uri = Uri.parse(uriStr)
            appContext.contentResolver.openInputStream(uri).use { stream ->
                requireNotNull(stream) { "Cannot open image uri: $uriStr" }
                val ref = storage.reference.child("products/$id/$index.jpg")
                ref.putStream(stream).await()
                urls += ref.downloadUrl.await().toString()
            }
        }
        val data = mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "price" to price,
            "images" to urls,
            "uploaderUid" to uploaderUid,
            "category" to category
        )
        firestore.collection("products").document(id.toString()).set(data).await()
        // also reflect locally
        dao.upsertAll(
            listOf(
                ProductEntity(
                    id = id,
                    title = title,
                    description = description,
                    price = price,
                    images = urls,
                    uploaderUid = uploaderUid,
                    category = category
                )
            )
        )
    }
}
