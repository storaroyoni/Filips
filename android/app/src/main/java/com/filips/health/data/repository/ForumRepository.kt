package com.filips.health.data.repository

import com.filips.health.data.model.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ForumRepository(
    private val firestore: FirebaseFirestore
) {
    suspend fun createPost(post: ForumPost) {
        firestore.collection("posts")
            .add(post)
            .await()
    }

    suspend fun getAllPosts(): List<ForumPost> {
        val snapshot = firestore.collection("posts")
            .orderBy("timestamp")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(ForumPost::class.java) }
    }

    suspend fun getPost(postId: String): ForumPost? {
        val document = firestore.collection("posts")
            .document(postId)
            .get()
            .await()

        return document.toObject(ForumPost::class.java)
    }
}