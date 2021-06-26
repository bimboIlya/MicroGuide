package com.example.microguide.mvp.ui

import android.content.Context
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.microguide.BaseActivity
import com.example.microguide.extensions.isDisabled
import com.example.microguide.extensions.showSnackBar
import com.example.microguide.extensions.startActivity
import com.example.microguide.mvp.presentation.MyRxPresenter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MvpRxActivity : BaseActivity(), MyView {

    companion object {

        fun start(context: Context) {
            context.startActivity<MvpRxActivity>()
        }
    }

    @Inject
    lateinit var presenter: MyRxPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter.attachView(this)
        supportActionBar?.title = "MVP RX"

        initListeners()
    }

    private fun initListeners() = with(binding) {
        loadPostButton.setOnClickListener { presenter.onLoadPostClicked() }
        loadUserButton.setOnClickListener { presenter.onLoadUserClicked() }
        loadCommentsButton.setOnClickListener { presenter.onLoadCommentsClicked() }
        throwButton.setOnClickListener { presenter.onThrowClicked() }
        retryButton.setOnClickListener { presenter.onRetryClicked() }
    }

    override fun showUserName(userName: String) {
        binding.userName.text = userName
    }

    override fun showPostBody(postBody: String) {
        binding.postBody.text = postBody
    }

    override fun showComments(comments: String) {
        binding.comments.text = comments
    }

    override fun showError(isError: Boolean) = with(binding) {
        retryButton.isVisible = isError
        loadUserButton.isGone = isError
        loadPostButton.isGone = isError
        loadCommentsButton.isGone = isError
        throwButton.isGone = isError
        userName.isGone = isError
        postBody.isGone = isError
        comments.isGone = isError
        if (isError) binding.root.showSnackBar()
    }

    override fun showLoading(isLoading: Boolean) = with(binding) {
        progressBar.isVisible = isLoading
        loadUserButton.isDisabled = isLoading
        loadPostButton.isDisabled = isLoading
        loadCommentsButton.isDisabled = isLoading
        throwButton.isDisabled = isLoading
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}