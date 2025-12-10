package com.tutedude.ecommerce.domain.repository

import com.tutedude.ecommerce.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun user(uid: String): Flow<User?>
    suspend fun upsert(user: User)
}
