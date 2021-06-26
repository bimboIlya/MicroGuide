package com.example.microguide.data.repository

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.model.UserModel
import com.example.microguide.data.network.RxPlaceholderApi
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import javax.inject.Inject

interface RxPlaceholderRepository {

    fun getPostById(postId: Int): Single<PostModel>

    fun getUserByPostId(postId: Int): Single<UserModel>

    fun getCommentsByPostsId(id: Int, idd: Int): Single<List<CommentModel>>

    fun loadWithError(): Single<Any>
}

/**
 * Проблему с навешиванием .subscribeOn(Schedulers.io()) на все асинхронные операции можно, конечно, решить
 * созданием слоя dataSource, который будет это всё разруливать, но надо же максимально преувеличенный пример привести)
 */
class RxPlaceholderRepositoryImpl @Inject constructor(
    private val api: RxPlaceholderApi
) : RxPlaceholderRepository {

    private companion object {

        const val THROW_DELAY = 800L
    }

    override fun getPostById(postId: Int): Single<PostModel> =
        api.getPostById(postId)
            .subscribeOn(Schedulers.io())

    override fun getUserByPostId(postId: Int): Single<UserModel> =
        api.getPostById(postId)
            .flatMap { post ->
                api.getUserById(post.userId)
                    .subscribeOn(Schedulers.io())
            }
            .subscribeOn(Schedulers.io())

    override fun getCommentsByPostsId(id: Int, idd: Int): Single<List<CommentModel>> =
        Singles.zip(
            api.getCommentsByPostId(id).subscribeOn(Schedulers.io()),
            api.getCommentsByPostId(idd).subscribeOn(Schedulers.io())
        ) { firstChunk, secondChunk ->
            firstChunk + secondChunk
        }
            .subscribeOn(Schedulers.io())

    override fun loadWithError(): Single<Any> =
        Single.create<Any> {
            Thread.sleep(THROW_DELAY)
            throw IOException()
        }
            .subscribeOn(Schedulers.io())
}