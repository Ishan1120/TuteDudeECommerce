package com.tutedude.ecommerce.domain.repository

import com.tutedude.ecommerce.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, displayName: String? = null): Result<Unit>
    suspend fun signOut()
}
