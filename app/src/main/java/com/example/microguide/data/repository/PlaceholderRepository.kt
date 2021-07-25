package com.example.microguide.data.repository

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.model.UserModel
import com.example.microguide.data.network.PlaceholderApi
import com.example.microguide.di.IoDispatcher
import kotlinx.coroutines.*
import java.io.IOException
import javax.inject.Inject

interface PlaceholderRepository {

    suspend fun getPostById(postId: Int): PostModel

    suspend fun getUserByPostId(postId: Int): UserModel

    suspend fun getCommentsByPostsId(id: Int, idd: Int): List<CommentModel>

    suspend fun getCommentsByPostId(id: Int) : List<CommentModel>

    suspend fun loadWithError()

    suspend fun getUserById(userId: Int): UserModel
}

/**
 * [CoroutineDispatcher] - пул потоков, на котором будут бегать корутины. Default для вычислений, IO - i/o, Main - мейн тред
 * Гугл рекомендует всегда инжектить диспатчеры для того, чтобы код можно было тестить (ахах), иначе многопоточность в тестах = гроб, гроб, кладбище, нестабильность
 */
class PlaceholderRepositoryImpl @Inject constructor(
    private val api: PlaceholderApi,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : PlaceholderRepository {

    private companion object {

        const val THROW_DELAY = 800L
    }

    /**
     * withContext исполняет блок кода в указанном контексте, складывая его с "наружним" контекстом
     * Удобно для переключения пула потоков, на котором работают корутины
     * Всегда можно быть уверенным, что твоя корутина не будет работать на главном потоке
     *
     * Так же дает возможность запустить новую корутину
     * Для запуска корутин можно использовать и функцию [coroutineScope], но итоговый скоуп наследует родительский контекст
     * Кажется, стоит упомянуть, что suspended функции можно вызывать только из других suspend функций или из скоупов)
     */
    override suspend fun getPostById(postId: Int): PostModel = withContext(dispatcher) {
        return@withContext api.getPostById(postId)
    }

    /**
     * Все корутины в блоке кода исполняются последовательно (кроме [async])
     * Когда исполнение доходит до suspend функции (перечёркнутая стрелочка слева), корутина приостанавливается
     * После того, как блок кода внутри suspend функции отработал,корутина возобновляется
     * Поэтому корутину кроме легковесного треда ещё называют коллбеком на стероидах
     * В отличие от Rx и обычных коллбеков всё линейно и красивенько
     */
    override suspend fun getUserByPostId(postId: Int): UserModel = withContext(dispatcher) {
        val post = api.getPostById(postId)
        val userId = post.userId

        return@withContext api.getUserById(userId)
    }

    /**
     * Пример неоч, но шо придумал))
     * Здесь блок кода оборачивается в [async], на выходе получаются Deferred-ы
     * При вызове [async] кроме создания Deferred ничего не происходит
     * Непосредственное исполнение кода начинается при вызове [Deferred.await]
     * В отличие от [launch], выполняющего код последовательно, [async] может выполнять код параллельно
     * Похоже на async из C#, Promise из JS, Future из Java и т.д.)
     */
    override suspend fun getCommentsByPostsId(id: Int, idd: Int): List<CommentModel> = withContext(dispatcher) {
        val firstCommentChunkDeferred = async { api.getCommentsByPostId(id) }
        val secondCommentChunkDeferred = async { api.getCommentsByPostId(idd) }

        return@withContext firstCommentChunkDeferred.await() + secondCommentChunkDeferred.await()
    }

    override suspend fun getCommentsByPostId(id: Int): List<CommentModel> = withContext(dispatcher) {
        api.getCommentsByPostId(id)
    }

    override suspend fun loadWithError() {
        delay(THROW_DELAY)
        throw IOException()
    }

    override suspend fun getUserById(userId: Int): UserModel = withContext(dispatcher) {
        return@withContext api.getUserById(userId)
    }
}