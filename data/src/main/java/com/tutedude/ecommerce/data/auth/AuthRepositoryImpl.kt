package com.tutedude.ecommerce.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.tutedude.ecommerce.domain.model.User
import com.tutedude.ecommerce.domain.repository.AuthRepository
import com.tutedude.ecommerce.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val users: UserRepository
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            val u = fa.currentUser
            trySend(
                u?.let { User(uid = it.uid, email = it.email, displayName = it.displayName) }
            )
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // ---------------- SIGN IN ----------------

    override suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        require(email.isNotBlank()) { "Email cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }

        auth.signInWithEmailAndPassword(email, password).await()
    }.map { }

    // ---------------- SIGN UP ----------------

    override suspend fun signUp(
        email: String,
        password: String,
        displayName: String?
    ): Result<Unit> = runCatching {

        require(email.isNotBlank()) { "Email cannot be empty" }
        require(password.isNotBlank()) { "Password cannot be empty" }

        // Create Firebase Auth User
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw Exception("User creation failed")

        // Update FirebaseAuth displayName if present
        if (!displayName.isNullOrBlank()) {
            val profileUpdate = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()
        }

        // Save user to Firestore
        users.upsert(
            User(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName ?: ""
            )
        )
    }.map { }

    // ---------------- SIGN OUT ----------------

    override suspend fun signOut() {
        auth.signOut()
    }
}
