package com.example.microguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.microguide.databinding.LayoutContentBinding

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var binding: LayoutContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LayoutContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}