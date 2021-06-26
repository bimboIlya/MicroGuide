package com.example.microguide.mvp.presentation

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.model.PostModel
import com.example.microguide.data.model.UserModel
import com.example.microguide.data.repository.RxPlaceholderRepository
import com.example.microguide.mvp.based.BasePresenter
import com.example.microguide.mvp.ui.MyView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MyRxPresenter @Inject constructor(
    private val repository: RxPlaceholderRepository,
) : BasePresenter<MyView>() {

    fun onLoadPostClicked() {
        view?.showLoading(true)

        repository.getPostById(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::onPostLoaded,
                onError = ::handleError
            )
            .addToDisposable()
    }

    private fun onPostLoaded(post: PostModel) {
        view?.showPostBody(post.body)

        view?.showLoading(false)
    }

    fun onLoadUserClicked() {
        loadUser()
    }

    private fun loadUser() {
        view?.showLoading(true)

        repository.getUserByPostId(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::onUserLoaded,
                onError = ::handleError
            )
            .addToDisposable()
    }

    private fun onUserLoaded(user: UserModel) {
        view?.showUserName(user.name)

        view?.showLoading(false)
    }

    fun onLoadCommentsClicked() {
        view?.showLoading(true)

        repository.getCommentsByPostsId(1, 2)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::onCommentsLoaded,
                onError = ::handleError
            )
            .addToDisposable()
    }

    private fun onCommentsLoaded(comments: List<CommentModel>) {
        val commentsText = comments
            .take(3)
            .joinToString(separator = "\n") { it.body }
        view?.showComments(commentsText)

        view?.showLoading(false)
    }

    fun onThrowClicked() {
        view?.showLoading(true)

        repository.loadWithError()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(onError = ::handleError)
            .addToDisposable()
    }

    fun onRetryClicked() {
        view?.showError(false)

        loadUser()
    }

    private fun handleError(error: Throwable) {
        view?.showLoading(false)
        view?.showError(true)
    }
}