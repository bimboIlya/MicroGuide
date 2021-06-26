package com.example.microguide.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

inline fun CoroutineScope.launchCatching(
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> Unit
) {
    this.launch {
        try {
            block()
        } catch (e: CancellationException) {
            // Без этого catch сломается механизм отмены корутин и т.н. паттерн structured concurrency
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            onError(e)
        }
    }
}