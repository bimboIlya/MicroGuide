package com.example.microguide.udf.var1.presentation

sealed class Action {

    object LoadUser : Action()

    object LoadPost : Action()

    object LoadComments : Action()

    object Throw : Action()

    object Retry : Action()
}
