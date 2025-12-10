package com.tutedude.ecommerce.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.tutedude.ecommerce.domain.model.User
import com.tutedude.ecommerce.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    // ---------------- LISTEN TO USER CHANGES ----------------
    override fun user(uid: String): Flow<User?> = callbackFlow {
        val docRef = firestore.collection("users").document(uid)
        val listener = docRef.addSnapshotListener { snap, error ->
            if (error != null) {
                Log.e("UserRepository", "Firestore listener error", error)
                trySend(null)
                return@addSnapshotListener
            }

            val user = if (snap != null && snap.exists()) {
                User(
                    uid = uid,
                    email = snap.getString("email") ?: "",
                    displayName = snap.getString("displayName") ?: ""
                )
            } else null

            trySend(user)
        }

        awaitClose { listener.remove() }
    }

    // ---------------- UPSERT (CREATE OR UPDATE) ----------------
    override suspend fun upsert(user: User) {
        try {
            val data = mapOf(
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: "")
            )

            firestore.collection("users")
                .document(user.uid)
                .set(data)
                .await()

            Log.d("UserRepository", "User saved successfully: ${user.uid}")

        } catch (e: Exception) {
            Log.e("UserRepository", "Error saving user", e)
            throw e // Let ViewModel handle or show message
        }
    }
}
