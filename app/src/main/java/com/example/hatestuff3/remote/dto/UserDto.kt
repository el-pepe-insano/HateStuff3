package com.example.hatestuff3.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Long? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    @SerializedName("role") val role: String? = "USER",
    val bio: String? = null,

    @SerializedName("profilePictureUri")
    val profilePictureUri: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)