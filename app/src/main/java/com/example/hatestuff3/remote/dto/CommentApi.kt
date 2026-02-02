package com.example.hatestuff3.data.remote

import com.example.hatestuff3.data.remote.dto.CommentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CommentApi {

    @GET("api/comments/post/{postId}")
    suspend fun getCommentsByPostId(
        @Path("postId") postId: Long
    ): List<CommentDto>

    @POST("api/comments")
    suspend fun createComment(
        @Body comment: CommentDto
    ): CommentDto
    @DELETE("api/comments/{id}")
    suspend fun deleteComment(
        @Path("id") id: Long
    ): Response<Unit>
}
