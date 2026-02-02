package com.example.hatestuff3.data.remote

import com.example.hatestuff3.data.remote.dto.PostDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface PostApi {

    @GET("api/posts")
    suspend fun getAllPosts(): List<PostDto>

    @Multipart
    @POST("api/posts")
    suspend fun createPost(
        @Part("content") content: RequestBody,
        @Part("userName") userName: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): Response<PostDto>

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Long): Response<Unit>

    @PUT("api/posts/{id}")
    suspend fun updatePost(@Path("id") id: Long, @Body post: PostDto): Response<PostDto>

    @GET("api/posts/user/{userId}")
    suspend fun getPostsByUser(@Path("userId") userId: Long): List<PostDto>

    @POST("api/posts/{id}/like")
    suspend fun likePost(
        @Path("id") id: Long,
        @Query("userName") userName: String
    ): Response<Unit>

}