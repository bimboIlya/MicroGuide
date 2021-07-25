package com.example.microguide.refactor.step1.ui

import android.content.Context
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.microguide.data.model.CommentModel
import com.example.microguide.data.repository.RxPlaceholderRepository
import com.example.microguide.extensions.showSnackBar
import com.example.microguide.extensions.startActivity
import com.example.microguide.refactor.common.ui.CommentAdapter
import com.example.microguide.refactor.common.ui.RefActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

// кринж: ui слой занимается загрузкой данных и полностью управляет своим состоянием
// ну и покушать от Rx (и вообще коллбеков) на ui: забыл отписаться = утечки памяти/краши
@AndroidEntryPoint
class Ref1Activity : RefActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<Ref1Activity>()
        }
    }

    @Inject
    lateinit var repository: RxPlaceholderRepository

    private val adapter = CommentAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() = with(binding) {
        val dividerDecoration = DividerItemDecoration(this@Ref1Activity, RecyclerView.VERTICAL)
        commentList.addItemDecoration(dividerDecoration)
        commentList.adapter = adapter
    }

    private fun setupListeners() = with(binding) {
        updateButton.setOnClickListener {
            val postId = input.text.toString().toIntOrNull()
            if (postId == null) {
                root.showSnackBar("Poop input")
                return@setOnClickListener
            }

            repository.getCommentsByPostId(postId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = ::onNewComments,
                    onError = ::onError,
                )
                .addToDisposables()

            it.isEnabled = false
            progressBar.isVisible = true
            emptyStub.isVisible = false
        }
    }

    private fun onNewComments(comments: List<CommentModel>) = with(binding) {
        updateButton.isEnabled = true
        progressBar.isVisible = false
        emptyStub.isVisible = comments.isEmpty()
        commentList.isVisible = comments.isNotEmpty()
        adapter.updateComments(comments)
    }

    private fun onError(error: Throwable) = with(binding) {
        progressBar.isVisible = false
        commentList.isVisible = false
        updateButton.isEnabled = true
    }
}