package com.example.microguide.refactor.common.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

abstract class BaseViewModel : ViewModel() {

    private val cd = CompositeDisposable()

    protected fun Disposable.addToDisposables() {
        addTo(cd)
    }

    override fun onCleared() {
        cd.clear()
        super.onCleared()
    }

}