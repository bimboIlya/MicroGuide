package com.example.microguide.refactor.step4.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.SingleMutableEvent
import com.example.microguide.extensions.invoke
import com.example.microguide.extensions.launchCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class Ref4ViewModel @Inject constructor(
    private val repository: PlaceholderRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Initial)
    val screenState = _screenState.asStateFlow()

    // следует помнить, что в текущей реализации, если ивент прилетит, когда активити STOPPED,
    // (по завершению асинхронной операции), то он просто проигнорится, т.к. это горячий поток
    private val _incorrectInputEvent = SingleMutableEvent<Unit>()
    val incorrectInputEvent = _incorrectInputEvent.asSharedFlow()

    fun updateComments(postId: String) {
        val id = postId.toIntOrNull()
        if (id == null) {
            _incorrectInputEvent(Unit)
            return
        }

        _screenState.tryEmit(ScreenState.Loading)

        viewModelScope.launchCatching(::onError) {
            val comments = repository.getCommentsByPostId(id)
            _screenState.emit(ScreenState.Content(comments))
        }
    }

    private fun onError(error: Throwable) {
        _screenState.tryEmit(ScreenState.Error)
    }
}