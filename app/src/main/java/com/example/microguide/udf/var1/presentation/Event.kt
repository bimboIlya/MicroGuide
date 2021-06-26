package com.example.microguide.udf.var1.presentation

sealed class Event {

    data class Error(val message: String) : Event()
}
