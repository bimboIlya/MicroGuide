package com.example.microguide.mvp.presentation

import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.launchCatching
import com.example.microguide.mvp.based.BasePresenter
import com.example.microguide.mvp.ui.MyView
import javax.inject.Inject

/**
 * Model-View-Presenter
 * Model - какой-то абстрактный источник данных, View - отображение(в случае ведроида - активити/фрагмент/кастомная вью/проч);
 * Presenter - штука, которая говорит вьюхе, когда отображать эти данные;
 * В MVP вью максимально тупая, знает только вещи, касаемые отрисовки (например, как показать текст, анимацию и тд)
 * Все взаимодействия передаёт презентеру (отсюда и коллбечные названия public методов презентера типа onButtonClick),
 * который уже сам решает, что с этим взаимодействием делать и при необходимости сообщает вью, что нужно сделать, вызывая её методы напрямую
 * Открытые методы ничего не возвращают
 *
 * Минусы
 * - Из-за того, что презентер держит ссылку на вью, могут происходить нехилые утечки памяти, поэтому нужно вовремя детачить вью
 * - Если экран сложный, то он может оказаться в неконсистентном состоянии из-за прямого дерганья методов вью (например, одновременно показывается лоадер и ошибка)
 * - После асинхронных операций могут вызываться методы вью, которая можеть быть в onStopped
 * Особенно это плохо при навигации. Если вызвать транзакцию фрагмента - вылетит исключение, стартануть активити - вызов просто проигнорируется
 * - Без черной магии сложно сохранять состояние экрана. Чтобы понять, о чем я, поверни телефон))
 *
 * Плюсы
 *  - Простой в исполнении и понимании паттерн
 *  - Огромный шаг вперед после God-Activity
 */
class MyPresenter @Inject constructor(
    private val repository: PlaceholderRepository,
) : BasePresenter<MyView>() {

    fun onLoadPostClicked() {
        presenterScope.launchCatching(::handleError) {
            view?.showLoading(true)

            val post = repository.getPostById(1)  // выполнится на IO тредах из-за withContext внутри с IO диспатчером
            view?.showPostBody(post.body)                // всё остальное выполнится на мейн треде, т.к. в контексте presenterScope Dispatcher.Main.immediate

            view?.showLoading(false)
        }
    }

    fun onLoadUserClicked() {
        loadUser()
    }

    private fun loadUser() {
        presenterScope.launchCatching(::handleError) {
            view?.showLoading(true)

            val user = repository.getUserByPostId(1)
            view?.showUserName(user.name)

            view?.showLoading(false)
        }
    }

    fun onLoadCommentsClicked() {
        presenterScope.launchCatching(::handleError) {
            view?.showLoading(true)

            val comments = repository.getCommentsByPostsId(1, 2)
                .take(3)
                .joinToString(separator = "\n") { it.body }
            view?.showComments(comments)

            view?.showLoading(false)
        }
    }

    fun onThrowClicked() {
        presenterScope.launchCatching(::handleError) {
            view?.showLoading(true)

            repository.loadWithError()

            view?.showLoading(false)
        }
    }

    fun onRetryClicked() {
        view?.showError(false)

        loadUser()
    }

    private fun handleError(error: Throwable) {
        view?.showLoading(false)
        view?.showError(true)
    }
}