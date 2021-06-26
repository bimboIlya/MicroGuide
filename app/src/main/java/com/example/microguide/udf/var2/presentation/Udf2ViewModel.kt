package com.example.microguide.udf.var2.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.extensions.launchCatching
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Просто альтернативный пример, потому что предыдущий мне показался корявым))
 */
@HiltViewModel
class Udf2ViewModel @Inject constructor(
    private val repository: PlaceholderRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ScreenState>(ScreenState.Input)
    val state = _state.asStateFlow()

    fun getUser(userIdInput: CharSequence) {
        viewModelScope.launchCatching(::handleError) {
            _state.value = ScreenState.Loading

            val userId = userIdInput.toString().toIntOrNull() ?: 1
            val user = repository.getUserById(userId)

            _state.value = ScreenState.Content(user)
        }
    }

    fun retry() {
        _state.value = ScreenState.Input
    }

    private fun handleError(error: Throwable) {
        _state.value = ScreenState.Error
    }
}