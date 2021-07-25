package com.example.microguide.refactor.step3.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.microguide.data.model.CommentModel
import com.example.microguide.extensions.showSnackBar
import com.example.microguide.extensions.startActivity
import com.example.microguide.refactor.common.extensions.observe
import com.example.microguide.refactor.common.ui.CommentAdapter
import com.example.microguide.refactor.common.ui.RefActivity
import com.example.microguide.refactor.step3.presentation.Ref3ViewModel
import com.example.microguide.refactor.step3.presentation.ScreenState
import dagger.hilt.android.AndroidEntryPoint

// полный копипаст со второго шага
@AndroidEntryPoint
class Ref3Activity : RefActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<Ref3Activity>()
        }
    }

    private val viewModel: Ref3ViewModel by viewModels()

    private val adapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupListeners() = with(binding) {
        updateButton.setOnClickListener {
            val postId = input.text.toString()
            viewModel.updateComments(postId)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        val dividerDecoration = DividerItemDecoration(this@Ref3Activity, RecyclerView.VERTICAL)
        commentList.addItemDecoration(dividerDecoration)
        commentList.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.screenState.observe(compositeDisposable, ::renderScreenState)
        viewModel.incorrectInputEvent.observe(compositeDisposable, ::onIncorrectInputEvent)
    }

    private fun renderScreenState(state: ScreenState) {
        when (state) {
            ScreenState.Loading -> renderLoadingState()
            ScreenState.Error -> renderErrorState()
            is ScreenState.Content -> renderContentState(state.comments)
        }
    }

    private fun renderLoadingState() = with(binding) {
        progressBar.isVisible = true
        commentList.isVisible = false
        emptyStub.isVisible = false
        updateButton.isEnabled = false
    }

    private fun renderErrorState() = with(binding) {
        progressBar.isVisible = false
        commentList.isVisible = false
        emptyStub.isVisible = false
        updateButton.isEnabled = true
    }

    private fun renderContentState(comments: List<CommentModel>) {
        when (comments.isEmpty()) {
            true -> renderEmptyState()
            false -> renderCommentsState(comments)
        }
    }

    private fun renderEmptyState() = with(binding) {
        progressBar.isVisible = false
        commentList.isVisible = false
        emptyStub.isVisible = true
        updateButton.isEnabled = true
    }

    private fun renderCommentsState(comments: List<CommentModel>) = with(binding) {
        progressBar.isVisible = false
        commentList.isVisible = true
        emptyStub.isVisible = false
        updateButton.isEnabled = true
        adapter.updateComments(comments)
    }

    private fun onIncorrectInputEvent() = with(binding) {
        root.showSnackBar("Input is poop")
    }
}