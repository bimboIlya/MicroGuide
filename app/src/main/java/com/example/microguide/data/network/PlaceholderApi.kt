package com.example.microguide.data.network

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.model.UserModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaceholderApi {

    @GET("posts/{postId}/")
    suspend fun getPostById(@Path("postId") postId: Int): PostModel

    @GET("users/{userId}/")
    suspend fun getUserById(@Path("userId") userId: Int): UserModel

    @GET("comments/")
    suspend fun getCommentsByPostId(@Query("postId") postId: Int): List<CommentModel>
}