package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AppProject
import com.example.data.model.ChatMessage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProjectRepository(
    private val db: FirebaseFirestore
) {
    // Secondary constructor to resolve the custom database ID safely
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    fun observeProjects(userId: String): Flow<List<AppProject>> {
        return db.collection("users")
            .document(userId)
            .collection("projects")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(AppProject::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun getProject(userId: String, projectId: String): AppProject? {
        val snapshot = db.collection("users")
            .document(userId)
            .collection("projects")
            .document(projectId)
            .get()
            .await()
        return snapshot.toObject(AppProject::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    }

    suspend fun saveProject(userId: String, project: AppProject): String {
        val collection = db.collection("users").document(userId).collection("projects")
        val docRef = if (project.id.isBlank()) collection.document() else collection.document(project.id)
        val projectToSave = project.copy(
            id = docRef.id,
            userId = userId,
            createdAt = if (project.id.isBlank()) null else project.createdAt,
            updatedAt = null // Evaluates @ServerTimestamp
        )
        docRef.set(projectToSave).await()
        return docRef.id
    }

    suspend fun deleteProject(userId: String, projectId: String) {
        db.collection("users")
            .document(userId)
            .collection("projects")
            .document(projectId)
            .delete()
            .await()
    }

    fun observeMessages(userId: String, projectId: String): Flow<List<ChatMessage>> {
        return db.collection("users")
            .document(userId)
            .collection("projects")
            .document(projectId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(ChatMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun addMessage(userId: String, projectId: String, message: ChatMessage): String {
        val colRef = db.collection("users")
            .document(userId)
            .collection("projects")
            .document(projectId)
            .collection("messages")
        val docRef = if (message.id.isBlank()) colRef.document() else colRef.document(message.id)
        val msgToSave = message.copy(
            id = docRef.id,
            userId = userId,
            projectId = projectId,
            timestamp = null // Evaluates @ServerTimestamp
        )
        docRef.set(msgToSave).await()
        return docRef.id
    }
}
