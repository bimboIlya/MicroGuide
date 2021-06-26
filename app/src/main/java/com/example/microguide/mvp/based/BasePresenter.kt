package com.example.microguide.mvp.based

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import timber.log.Timber

abstract class BasePresenter<V : BaseView> {

    /**
     * Тут ручками создаем CoroutineScope, lifecycle которого привязан к презентеру
     * Все запущенные корутины внутри этого скоупа отменятся при вызове detachView()
     * Dispatchers.Main.immediate - все корутины будут стартовать на мейн треде, поэтому нужно не забывать менять потоки, иначе можно словить ANR
     * SupervisorJob() - в отличии от просто Job(), исключения в дочерних корутинах не отменят родительский скоуп. Подробнее в доках))
     * CoroutineExceptionHandler - ловит эксепшены, которые не обработались в try/catch. Не стоит на них делать обработку ошибок, они не для этого.
     *   Да и вообще, лучше его не втыкать
     */
    protected val presenterScope: CoroutineScope =
        CoroutineScope(Dispatchers.Main.immediate + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        })

    private val compositeDisposable = CompositeDisposable()

    protected var view: V? = null

    fun attachView(view: V) {
        this.view = view
        onAttachView()
    }

    protected open fun onAttachView() = Unit

    fun detachView() {
        onDetachView()
        presenterScope.cancel()
        compositeDisposable.dispose()
        this.view = null
    }

    protected open fun onDetachView() = Unit

    protected fun Disposable.addToDisposable() {
        compositeDisposable.add(this)
    }
}