package com.example.microguide.refactor.step4.presentation

import com.example.microguide.data.model.CommentModel

sealed class ScreenState {

    object Loading: ScreenState()

    data class Content(val comments: List<CommentModel>) : ScreenState()

    object Error : ScreenState()

    companion object {

        val Initial = Content(emptyList())
    }
}
