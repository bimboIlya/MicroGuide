package com.example.microguide.refactor.common.extensions

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.Subject

// тут вылетит NPE, если в subject что-то забыл null
fun <T : Any> Subject<T>.observe(compositeDisposable: CompositeDisposable, block: (T) -> Unit) {
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribe(block)
        .addTo(compositeDisposable)
}

fun Subject<Unit>.observe(compositeDisposable: CompositeDisposable, block: () -> Unit) {
    this.observeOn(AndroidSchedulers.mainThread())
        .subscribe { block() }
        .addTo(compositeDisposable)
}