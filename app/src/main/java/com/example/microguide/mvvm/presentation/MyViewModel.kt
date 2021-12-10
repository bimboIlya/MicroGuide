package com.example.microguide.mvvm.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.SingleMutableEvent
import com.example.microguide.extensions.invoke
import com.example.microguide.extensions.launchCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Model-ViewModel-View
 * Model и View - то же самое, что в MVP. ViewModel - сущность, у которой есть открытые поля с нужными для вью данными (обычно приведённая к нужному виду UI моделька)
 * Основное отличие от MVP в том, что ViewModel ничего не знает о вью. Из этого следует, что вьюмодель не может говорить вью, что делать, как в MVP
 * Вместо этого, вью обсервит данные и ивенты вьюмодели (KVO в Swift'e?)
 * Канонично для этого используется механизм связывания данных (DataBinding), но можно и просто с observable полями))
 * В отличии от MVP, где открытые методы назывались как коллбеки, в MVVM методы обзываются действиями (например, loadData)
 * Методы ничего не возвращают, все данные возвращаются через observable поля (ясен пень, если данные заданы при инициализации и не изменятся, то можно сделать просто открытым полем)
 *
 * Минусы:
 * - Никуда не делась вероятность неконсистентного состояния экрана
 * - Куча полей во вьюмодели, если данных много)
 *
 * Плюсы:
 * - Бóльшая возможность переиспользования за счет отсутствия ссылки на вью (редко актуально)
 * - Вьюмодели не нужно рулить состоянием вью напрямую
 * - Проще тестировать(ахах), т.к. VM не привязана к View
 *
 * Специфика ведроида
 * Лайфсайкл активити и особенно фрагмента в андроиде - боль. Вьюмодель от гугла неплохо её облегчает
 * VM привязывается к жизненному циклу активити/фрагмента (они наследуют [ViewModelStoreOwner], по приколу можно сделать свою реализацию) и уничтожается только
 * когда [ViewModelStoreOwner] уничтожается с концами (например, навигация с экрана назад)
 * За счет этого VM, и, соответственно, данные внутри неё, могут переживать смену конфигурации
 * (список всех возможных изменений конфигурации (по сути, рестарта активити) https://developer.android.com/guide/topics/manifest/activity-element#config)
 * За счет того, что вью сама обсервит вьюмодель и устанавливает свои поля, то после смены конфигуации вьюшка подхватит все нужные данные из VM
 * Так же за счет этого решается проблема с асинхронными операциями, которая встречается в MVP на андроиде, которую я описал в презентере
 * Вью consumes ивенты только тогда, когда она сможет их обработать
 *
 * Корутины
 * В отличие от презентера, у вьюмодели есть скоуп [viewModelScope], который поставляется с зависимостью "androidx.lifecycle:lifecycle-viewmodel-ktx"
 * Оменяется в onCleared(), т.е. когда [ViewModelStoreOwner] (активити, фрагмент, кастомная шляпа) завершается с концами (например, выходим с экрана назад)
 * Как уже можно было заметить, корутины всегда запускаются внутри какого-либо скоупа. Это помогает отменять корутины, запущенные внутри скоупа
 * при его отмене (скоуп привязывается к объекту с определённым лайфсайклом, например, активити и отменяется в конце лайфсайкла)
 * Также корутины следуют паттерну structured concurrency, т.е. при отмене родительского скоупа/корутины отменяются все дочерние корутины
 *
 * Flow
 * Flow - реализация спецификации Reactive Stream котлина, работает на корутинах. Аналог [Flowable] из RxJava. Имеет встроенный Backpressure handling
 * за счёт механизма приостановки корутин
 * SharedFlow - горячий поток. Очень удобно использовать для ивентов, т.к. они должны быть consumed только один раз каждым подписчиком
 * StateFlow - Flow, который в любой момент времени имеет одно и только одно значение, которое можно получить через .value. Аналог [BehaviorSubject]
 * StateFlow удобно использовать для обсервинга данных. В любой момент можно получить текущее значение, а как только придет новое - вызовется коллбек
 * В отличие от SharedFlow требует значения для инициализации
 */
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: PlaceholderRepository,
) : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _postBody = MutableStateFlow("")
    val postBody = _postBody.asStateFlow()

    private val _comments = MutableStateFlow("")
    val comments = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError = _isError.asStateFlow()

    private val _errorEvent = SingleMutableEvent<Unit>()
    val errorEvent = _errorEvent.asSharedFlow()

    fun loadUser() {
        viewModelScope.launchCatching(::handleError) {
            _isLoading.value = true

            val user = repository.getUserByPostId(1)
            _userName.value = user.name

            _isLoading.value = false
        }
    }

    fun loadPost() {
        viewModelScope.launchCatching(::handleError) {
            _isLoading.value = true

            val post = repository.getPostById(1)
            _postBody.value = post.body

            _isLoading.value = false
        }
    }

    fun loadComments() {
        viewModelScope.launchCatching(::handleError) {
            _isLoading.value = true

            _comments.value = repository.getCommentsByPostsId(1, 2)
                .take(3)
                .joinToString(separator = "\n") { it.body }

            _isLoading.value = false
        }
    }

    fun throwError() {
        viewModelScope.launchCatching(::handleError) {
            _isLoading.value = true

            repository.loadWithError()

            _isLoading.value = false
        }
    }

    fun retry() {
        _isError.value = false

        loadUser()
    }

    private fun handleError(error: Throwable) {
        _isLoading.value = false
        _isError.value = true
        _errorEvent(Unit)
    }
}