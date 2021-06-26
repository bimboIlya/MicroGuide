package com.example.microguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.microguide.databinding.ActivityMainBinding
import com.example.microguide.mvp.ui.MvpActivity
import com.example.microguide.mvp.ui.MvpRxActivity
import com.example.microguide.mvvm.ui.MvvmActivity
import com.example.microguide.udf.var1.ui.UdfActivity
import com.example.microguide.udf.var2.ui.Udf2Activity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() = with(binding) {
        mvpButton.setOnClickListener { MvpActivity.start(this@MainActivity) }
        mvpRxButton.setOnClickListener { MvpRxActivity.start(this@MainActivity) }
        mvvmButton.setOnClickListener { MvvmActivity.start(this@MainActivity) }
        udfButton.setOnClickListener { UdfActivity.start(this@MainActivity) }
        udf2Button.setOnClickListener { Udf2Activity.start(this@MainActivity) }
    }
}