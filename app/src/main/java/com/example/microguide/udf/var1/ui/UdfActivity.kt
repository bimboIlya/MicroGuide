package com.example.microguide.udf.var1.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.microguide.BaseActivity
import com.example.microguide.extensions.showSnackBar
import com.example.microguide.extensions.startActivity
import com.example.microguide.extensions.subscribe
import com.example.microguide.udf.var1.presentation.Action
import com.example.microguide.udf.var1.presentation.Event
import com.example.microguide.udf.var1.presentation.ScreenState
import com.example.microguide.udf.var1.presentation.UdfViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UdfActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<UdfActivity>()
        }
    }

    private val viewModel: UdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "UDF"

        initListeners()
        subscribeToViewModel()
    }

    private fun initListeners() = with(binding) {
        loadUserButton.setOnClickListener { viewModel.submitAction(Action.LoadUser) }
        loadPostButton.setOnClickListener { viewModel.submitAction(Action.LoadPost) }
        loadCommentsButton.setOnClickListener { viewModel.submitAction(Action.LoadComments) }
        throwButton.setOnClickListener { viewModel.submitAction(Action.Throw) }
        retryButton.setOnClickListener { viewModel.submitAction(Action.Retry) }
    }

    private fun subscribeToViewModel() {
        viewModel.screenState.subscribe(lifecycle, ::renderState)
        viewModel.events.subscribe(lifecycle, ::onEvent)
    }

    private fun renderState(state: ScreenState) {
        when {
            state.isError -> renderErrorState()
            state.isLoading -> renderLoadingState()
            else -> renderContentState(state)
        }
    }

    private fun renderErrorState() = with(binding) {
        progressBar.isVisible = false
        retryButton.isVisible = true
        showButtons(false)
        enableButtons(false)
        showContent(false)
    }

    private fun renderLoadingState() = with(binding) {
        progressBar.isVisible = true
        retryButton.isVisible = false
        showButtons(true)
        enableButtons(false)
        showContent(true)
    }

    private fun renderContentState(content: ScreenState) = with(binding) {
        progressBar.isVisible = false
        retryButton.isVisible = false
        showButtons(true)
        enableButtons(true)
        showContent(true)
        userName.text = content.userName
        postBody.text = content.postBody
        comments.text = content.comments
    }

    private fun showButtons(isVisible: Boolean) = with(binding) {
        loadUserButton.isVisible = isVisible
        loadPostButton.isVisible = isVisible
        loadCommentsButton.isVisible = isVisible
        throwButton.isVisible = isVisible
    }

    private fun enableButtons(isEnabled: Boolean) = with(binding) {
        loadUserButton.isEnabled = isEnabled
        loadPostButton.isEnabled = isEnabled
        loadCommentsButton.isEnabled = isEnabled
        throwButton.isEnabled = isEnabled
    }

    private fun showContent(isVisible: Boolean) = with(binding) {
        userName.isVisible = isVisible
        postBody.isVisible = isVisible
        comments.isVisible = isVisible
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Event.Error -> showErrorMessage(event.message)
        }
    }

    private fun showErrorMessage(message: String) {
        binding.root.showSnackBar(message)
    }
}