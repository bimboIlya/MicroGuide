package com.example.microguide.refactor.step2.presentation

import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.repository.RxPlaceholderRepository
import com.example.microguide.refactor.common.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Тут порекомендовал бы переехать на Hilt(андроид-обертка над даггером), иначе придется
 * потанцевать с бубном для того, чтобы инжектить вьюмодель в активити/фрагмент
 * А уж в чистый Composable запихивать VM без hilt`a вообще смэрт
 */
@HiltViewModel
class Ref2ViewModel @Inject constructor(
    private val repository: RxPlaceholderRepository,
) : BaseViewModel() {

    val screenState = BehaviorSubject.createDefault<ScreenState>(ScreenState.Initial)

    // следует помнить, что в текущей реализации, если ивент прилетит, когда активити STOPPED,
    // (по завершению асинхронной операции), то он просто проигнорится, т.к. это вроде горячего потока
    val incorrectInputEvent = PublishSubject.create<Unit>()

    fun updateComments(postId: String) {
        val id = postId.toIntOrNull()
        if (id == null) {
            incorrectInputEvent.onNext(Unit)
            return
        }

        screenState.onNext(ScreenState.Loading)

        repository.getCommentsByPostId(id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = ::setContentState,
                onError = ::onError
            )
            .addToDisposables()
    }

    private fun setContentState(comments: List<CommentModel>) {
        screenState.onNext(ScreenState.Content(comments))
    }

    private fun onError(error: Throwable) {
        screenState.onNext(ScreenState.Error)
    }
}