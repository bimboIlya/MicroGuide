package com.example.microguide.mvp.ui

import com.example.microguide.mvp.based.BaseView

interface MyView : BaseView {

    fun showUserName(userName: String)

    fun showPostBody(postBody: String)

    fun showComments(comments: String)

    fun showError(isError: Boolean)

    fun showLoading(isLoading: Boolean)
}