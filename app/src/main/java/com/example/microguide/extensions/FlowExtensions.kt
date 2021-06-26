package com.example.microguide.extensions

import androidx.lifecycle.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

/**
 * Экстеншны [subscribe] для удобного обсервинга данных
 * Корутина, собирающая данные флоу, стартует только когда лайфсайкл в состоянии [Lifecycle.State.RESUMED] (потому что я передал его в функцию))
 * Автоматически отписывается от флоу, если находится в состоянии "ниже"
 * А работает всё это чудо за счет [Lifecycle.repeatOnLifecycle], добавленном в "androidx.lifecycle:lifecycle-runtime-ktx" в версии 2.4.0-alpha01
 *
 * (Краткий экскурс в историю, зачем repeatOnLifecycle вообще добавили:
 * Корутину в активити/фрагментов можно запустить с помощью функций типа [LifecycleCoroutineScope.launchWhenResumed]
 * Проблема в том, что когда лайфсайкл переходил в состояние "ниже", указанного в функции, корутина приостанавливалась, но не отменялась
 * Флоу всё ещё мог отправлять данные, т.к. у него был активный подписчик, что тратило память в процессорное время)
 *
 * Вся эта красота похоже на такую штуку, как [LiveData] - observable обертка, которая учитывает лайфсайкл своих подписчиков
 * Нынче для людей, которые пользуются корутинами, неактуальна
 *
 * Все эти параметры для [MutableSharedFlow] нужны, чтобы можно было эмитить значение не из корутины
 * Так же просто для удобства
 */
inline fun <T> Flow<T>.subscribe(lifecycle: Lifecycle, crossinline block: (T) -> Unit) {
    flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .onEach { block(it) }
        .launchIn(lifecycle.coroutineScope)
}

inline fun Flow<Unit>.subscribe(lifecycle: Lifecycle, crossinline block: () -> Unit) {
    flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
        .onEach { block() }
        .launchIn(lifecycle.coroutineScope)
}

fun <T> SingleMutableEvent() = MutableSharedFlow<T>(replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

operator fun <T> MutableSharedFlow<T>.invoke(value: T) {
    tryEmit(value)
}