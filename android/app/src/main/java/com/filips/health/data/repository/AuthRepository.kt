package com.filips.health.data.repository

import com.filips.health.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("Login failed")
    }

    suspend fun register(email: String, password: String, username: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: throw IllegalStateException("Registration failed")

        // Create user in Firestore
        val user = User(
            id = firebaseUser.uid,
            email = email,
            username = username
        )

        firestore.collection("users")
            .document(user.id)
            .set(user)
            .await()

        return firebaseUser
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val document = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()
        return document.toObject(User::class.java)
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun updateUserProfile(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(user)
            .await()
    }
}