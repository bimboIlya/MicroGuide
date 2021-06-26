package com.example.microguide.udf.var1.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.SingleMutableEvent
import com.example.microguide.extensions.invoke
import com.example.microguide.extensions.launchCatching
import com.example.microguide.udf.var1.presentation.Action.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Дисклеймер: я сам только начинаю знакомиться с этой архитектурой, поэтому мог обосраться)
 *
 * (подобие) Model-View-Intent (но не [android.content.Intent])
 * Model, View - понятно. Intent - какое-либо пользовательское взаимодействие
 * Суть паттерна в том, что пользовательское взаимодействие каким-то образом обрабатывается (обычно reducer'ом), что порождает state,
 * который уже отображается вью (всё немного сложнее, но в целом сводится к этому)
 * Нужно помнить, что стейт иммутабельный, поэтому каждый интент создает новый стейт, а не меняет старый
 *
 * Необязательно в качестве контроллера использовать вьюмодель, но она просто-напросто удобная)
 *
 * Минусы:
 * - Тяжеловато въехать
 * - За счет иммутабельности могут появляться некоторые проблемы с производительностью
 *   На этом примере: при загрузке юзера создается новый стейт, в котором поменяется только юзернейм
 *   При этом ещё перерисуются комменты и пост (лишняя работа, тк они уже были на экране). Как решение - написать/взять кастомные текствью, игноряющие повторяющийся текст. Или забить))
 *   Ещё критичнее всё становится с огромными списками, но это тоже решаемо с помощью DiffUtils
 *   Jetpack Compose решает все эти проблемы, тк он не перерисовывает одинаковые значения. А ещё он современный, модный, хайповый и декларативный
 *
 * Плюсы:
 * - Единый стейт, который является single source of truth для отрисовки состояния экрана. Всегда консистентное состояние экрана. А шо ещё нам фронтендерам надо?
 */
@HiltViewModel
class UdfViewModel @Inject constructor(
    private val repository: PlaceholderRepository,
) : ViewModel() {

    private val _screenState = MutableStateFlow(ScreenState.INITIAL)
    val screenState = _screenState.asStateFlow()

    private val _events = SingleMutableEvent<Event>()
    val events = _events.asSharedFlow()

    fun submitAction(action: Action) {
        when (action) {
            LoadUser -> loadUser()
            LoadPost -> loadPost()
            LoadComments -> loadComments()
            Throw -> throwError()
            Retry -> retry()
        }
    }

    private fun loadUser() {
        viewModelScope.launchCatching(::handleError) {
            _screenState.value = _screenState.value.copy(isLoading = true, isError = false)

            val user = repository.getUserByPostId(1)
            _screenState.value = _screenState.value.copy(userName = user.name, isLoading = false)
        }
    }

    private fun loadPost() {
        viewModelScope.launchCatching(::handleError) {
            _screenState.value = _screenState.value.copy(isLoading = true, isError = false)

            val post = repository.getPostById(1)
            _screenState.value = _screenState.value.copy(postBody = post.body, isLoading = false)
        }
    }

    private fun loadComments() {
        viewModelScope.launchCatching(::handleError) {
            _screenState.value = _screenState.value.copy(isLoading = true, isError = false)

            val comments = repository.getCommentsByPostsId(1, 2)
                .take(3)
                .joinToString(separator = "\n") { it.body }
            _screenState.value = _screenState.value.copy(comments = comments, isLoading = false)
        }
    }

    private fun throwError() {
        viewModelScope.launchCatching(::handleError) {
            _screenState.value = _screenState.value.copy(isLoading = true, isError = false)

            repository.loadWithError()
        }
    }

    private fun retry() {
        loadUser()
    }

    private fun handleError(error: Throwable) {
        _events(Event.Error(message = "lole poop"))
        _screenState.value = _screenState.value.copy(isLoading = false, isError = true)
    }
}