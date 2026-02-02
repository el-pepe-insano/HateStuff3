package com.example.hatestuff3.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemoteModule {

    // --- CONFIGURACIÓN DEL CLIENTE CON LOGS ---
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // --- 1. CONFIGURACIÓN USUARIOS (Puerto 8081) ---
    private const val BASE_URL_USERS = "http://192.168.0.165:8081/"

    private val retrofitUsers: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_USERS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy {
        retrofitUsers.create(UserApi::class.java)
    }

    // --- 2. CONFIGURACIÓN POSTS (Puerto 8082) ---
    private const val BASE_URL_POSTS = "http://192.168.0.165:8082/"

    private val retrofitPosts: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_POSTS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val postApi: PostApi by lazy {
        retrofitPosts.create(PostApi::class.java)
    }

    // --- 3. CONFIGURACIÓN COMENTARIOS (Puerto 8083) ---
    private const val BASE_URL_COMMENTS = "http://192.168.0.165:8083/"

    private val retrofitComments: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_COMMENTS)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val commentApi: CommentApi by lazy {
        retrofitComments.create(CommentApi::class.java)
    }
}