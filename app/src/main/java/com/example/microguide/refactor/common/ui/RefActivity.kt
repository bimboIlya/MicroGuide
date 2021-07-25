package com.example.microguide.refactor.common.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.microguide.databinding.ActivityRefBinding
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

abstract class RefActivity : AppCompatActivity() {

    lateinit var binding: ActivityRefBinding

    protected val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRefBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    protected fun Disposable.addToDisposables() {
        addTo(compositeDisposable)
    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }
}