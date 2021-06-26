package com.example.microguide.data.network

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.model.UserModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RxPlaceholderApi {

    @GET("posts/{postId}/")
    fun getPostById(@Path("postId") postId: Int): Single<PostModel>

    @GET("users/{userId}")
    fun getUserById(@Path("userId") userId: Int): Single<UserModel>

    @GET("comments/")
    fun getCommentsByPostId(@Query("postId") postId: Int): Single<List<CommentModel>>
}