package com.example.microguide.udf.var1.presentation

data class ScreenState(
    val userName: String,
    val postBody: String,
    val comments: String,
    val isLoading: Boolean,
    val isError: Boolean,
) {

    companion object {

        val INITIAL = ScreenState(
            userName = "",
            postBody = "",
            comments = "",
            isLoading = false,
            isError = false,
        )
    }
}
