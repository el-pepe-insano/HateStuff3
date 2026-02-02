package com.example.hatestuff3.data.local.database.repository

import android.content.Context
import android.net.Uri
import com.example.hatestuff3.data.remote.CommentApi
import com.example.hatestuff3.data.remote.PostApi
import com.example.hatestuff3.data.remote.dto.CommentDto
import com.example.hatestuff3.data.remote.dto.PostDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PostRepository(
    private val postApi: PostApi,
    private val commentApi: CommentApi,
    private val context: Context
) {

    // 1. OBTENER TODOS LOS POSTS (Desde el puerto 8081)
    suspend fun getAllPosts(): List<PostDto> {
        return postApi.getAllPosts()
    }

    // 2. CREAR POST CON IMAGEN (Multipart)
    suspend fun createPost(content: String, userName: String, imageUri: Uri?) {
        val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())
        val userPart = userName.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = if (imageUri != null) {
            prepareFilePart("image", imageUri, context)
        } else {
            null
        }

        postApi.createPost(contentPart, userPart, imagePart)
    }

    // 3. DAR LIKE
    suspend fun likePost(postId: Long, userName: String) {
        postApi.likePost(postId, userName)
    }

    // 4. OBTENER COMENTARIOS (Desde el puerto 8082)
    suspend fun getComments(postId: Long): List<CommentDto> {
        return commentApi.getCommentsByPostId(postId)
    }

    // 5. CREAR COMENTARIO
    suspend fun createComment(comment: CommentDto) {
        commentApi.createComment(comment)
    }

    // 6. ELIMINAR COMENTARIO (NUEVO - Aquí estaba el error)
    suspend fun deleteComment(commentId: Long) {
        // Llamamos a la API de comentarios para borrar por ID
        commentApi.deleteComment(commentId)
    }

    // 7. ELIMINAR POST (NUEVO)
    suspend fun deletePost(postId: Long) {
        postApi.deletePost(postId)
    }

    // 8. ACTUALIZAR POST (NUEVO)
    suspend fun updatePost(postId: Long, post: PostDto) {
        postApi.updatePost(postId, post)
    }


    // --- FUNCIÓN AUXILIAR PARA PROCESAR IMÁGENES ---
    private fun prepareFilePart(partName: String, fileUri: Uri, context: Context): MultipartBody.Part {
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        val inputStream = context.contentResolver.openInputStream(fileUri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}