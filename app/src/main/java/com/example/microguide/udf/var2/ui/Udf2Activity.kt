package com.example.microguide.udf.var2.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.microguide.databinding.ActivityUdf2Binding
import com.example.microguide.extensions.hideKeyboard
import com.example.microguide.extensions.startActivity
import com.example.microguide.extensions.subscribe
import com.example.microguide.udf.var2.presentation.ScreenState
import com.example.microguide.udf.var2.presentation.Udf2ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Udf2Activity : AppCompatActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity<Udf2Activity>()
        }
    }

    private lateinit var binding: ActivityUdf2Binding
    private val viewModel: Udf2ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUdf2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "UDF2"

        initListeners()
        subscribeToViewModel()
    }

    private fun initListeners() = with(binding) {
        retryButton.setOnClickListener { viewModel.retry() }
        getAnotherButton.setOnClickListener { viewModel.retry() }
        getUserButton.setOnClickListener {
            hideKeyboard()
            viewModel.getUser(userIdInput.text)
        }
    }

    private fun subscribeToViewModel() {
        viewModel.state.subscribe(lifecycle, ::renderState)
    }

    private fun renderState(state: ScreenState) {
        when (state) {
            ScreenState.Input -> renderInputState()
            ScreenState.Loading -> renderLoadingState()
            ScreenState.Error -> renderErrorState()
            is ScreenState.Content -> renderContentState(state)
        }
    }

    private fun renderInputState() = with(binding) {
        progressBar.isVisible = false
        userIdInput.isVisible = true
        getUserButton.isVisible = true
        userName.isVisible = false
        userEmail.isVisible = false
        getAnotherButton.isVisible = false
        retryButton.isVisible = false
    }

    private fun renderLoadingState() = with(binding) {
        progressBar.isVisible = true
        userIdInput.isVisible = false
        getUserButton.isVisible = false
        userName.isVisible = false
        userEmail.isVisible = false
        getAnotherButton.isVisible = false
        retryButton.isVisible = false
    }

    private fun renderErrorState() = with(binding) {
        progressBar.isVisible = false
        userIdInput.isVisible = false
        getUserButton.isVisible = false
        userName.isVisible = false
        userEmail.isVisible = false
        getAnotherButton.isVisible = false
        retryButton.isVisible = true
    }

    private fun renderContentState(state: ScreenState.Content) = with(binding) {
        progressBar.isVisible = false
        userIdInput.isVisible = false
        getUserButton.isVisible = false
        userName.isVisible = true
        userEmail.isVisible = true
        getAnotherButton.isVisible = true
        retryButton.isVisible = false
        userName.text = state.userModel.name
        userEmail.text = state.userModel.email
    }
}