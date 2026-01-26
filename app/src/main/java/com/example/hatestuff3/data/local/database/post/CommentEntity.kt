package com.example.hatestuff3.data.local.database.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Long,
    val userName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)