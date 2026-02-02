package com.example.hatestuff3.data.remote.dto

data class PostDto(
    val id: Long? = null,
    val userName: String,
    val content: String,
    val imageUri: String? = null,
    val likes: Int = 0,
    val likedBy: List<String> = emptyList()
)