package com.example.microguide.refactor.step3.presentation

import androidx.lifecycle.viewModelScope
import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.launchCatching
import com.example.microguide.refactor.common.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

// для ui слоя все также торчат RxJava Subjects, а "бэкэнд" уже полностью на корутинах
@HiltViewModel
class Ref3ViewModel @Inject constructor(
    private val repository: PlaceholderRepository,
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

        viewModelScope.launchCatching(::onError) {
            val comments =  repository.getCommentsByPostId(id)
            screenState.onNext(ScreenState.Content(comments))
        }
    }

    private fun onError(error: Throwable) {
        screenState.onNext(ScreenState.Error)
    }
}