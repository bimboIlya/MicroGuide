package com.example.microguide.udf.var2.presentation

import com.example.microguide.data.model.UserModel

sealed class ScreenState {

    object Input : ScreenState()

    data class Content(val userModel: UserModel) : ScreenState()

    object Loading : ScreenState()

    object Error : ScreenState()
}
