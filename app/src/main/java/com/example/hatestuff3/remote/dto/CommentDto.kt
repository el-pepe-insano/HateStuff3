package com.example.hatestuff3.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CommentDto(
    // Null al enviar (el server lo crea), Long al recibir
    @SerializedName("id")
    val id: Long? = null,

    // Obligatorio: a qué post pertenece
    @SerializedName("postId")
    val postId: Long,

    // Obligatorio: quién comenta
    @SerializedName("userName")
    val userName: String,

    // El texto del comentario
    @SerializedName("content")
    val content: String,

)