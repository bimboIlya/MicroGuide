package com.example.microguide.mvvm.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.microguide.BaseActivity
import com.example.microguide.extensions.isDisabled
import com.example.microguide.extensions.showSnackBar
import com.example.microguide.extensions.startActivity
import com.example.microguide.extensions.subscribe
import com.example.microguide.mvvm.presentation.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MvvmActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<MvvmActivity>()
        }
    }

    private val viewModel: MyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "MVVM"

        initListeners()
        subscribeToViewModel()
    }

    private fun initListeners() = with(binding) {
        loadUserButton.setOnClickListener { viewModel.loadUser() }
        loadPostButton.setOnClickListener { viewModel.loadPost() }
        loadCommentsButton.setOnClickListener { viewModel.loadComments() }
        throwButton.setOnClickListener { viewModel.throwError() }
        retryButton.setOnClickListener { viewModel.retry() }
    }

    private fun subscribeToViewModel() {
        viewModel.userName.subscribe(lifecycle, ::setUserName)
        viewModel.postBody.subscribe(lifecycle, ::setPostBody)
        viewModel.comments.subscribe(lifecycle, ::setComments)
        viewModel.isLoading.subscribe(lifecycle, ::showLoading)
        viewModel.isError.subscribe(lifecycle, ::showError)
        viewModel.errorEvent.subscribe(lifecycle, ::onErrorEvent)
    }

    private fun setUserName(userName: String) {
        binding.userName.text = userName
    }

    private fun setPostBody(postBody: String) {
        binding.postBody.text = postBody
    }

    private fun setComments(comments: String) {
        binding.comments.text = comments
    }

    private fun showLoading(isLoading: Boolean) = with(binding) {
        progressBar.isVisible = isLoading
        loadUserButton.isDisabled = isLoading
        loadPostButton.isDisabled = isLoading
        loadCommentsButton.isDisabled = isLoading
        throwButton.isDisabled = isLoading
    }

    private fun showError(isError: Boolean) = with(binding) {
        retryButton.isVisible = isError
        loadUserButton.isGone = isError
        loadPostButton.isGone = isError
        loadCommentsButton.isGone = isError
        throwButton.isGone = isError
        userName.isGone = isError
        postBody.isGone = isError
        comments.isGone = isError
    }

    private fun onErrorEvent() {
        binding.root.showSnackBar()
    }
}