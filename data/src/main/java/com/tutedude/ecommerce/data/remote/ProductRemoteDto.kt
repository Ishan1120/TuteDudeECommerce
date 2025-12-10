package com.tutedude.ecommerce.data.remote

import com.google.firebase.firestore.DocumentSnapshot

data class ProductRemoteDto(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val images: List<String> = emptyList(),
    val uploaderUid: String? = null,
    val category: String? = null
) {
    companion object {
        fun fromDoc(doc: DocumentSnapshot): ProductRemoteDto {
            val id = (doc.getLong("id") ?: 0L)
            val title = doc.getString("title") ?: ""
            val description = doc.getString("description") ?: ""
            val price = (doc.getDouble("price") ?: 0.0)
            val images = doc.get("images") as? List<*> ?: emptyList<Any>()
            val uploaderUid = doc.getString("uploaderUid")
            val category = doc.getString("category")
            return ProductRemoteDto(
                id = id,
                title = title,
                description = description,
                price = price,
                images = images.filterIsInstance<String>(),
                uploaderUid = uploaderUid,
                category = category
            )
        }
    }
}
