package com.example.microguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.microguide.databinding.ActivityMainBinding
import com.example.microguide.mvp.ui.MvpActivity
import com.example.microguide.mvp.ui.MvpRxActivity
import com.example.microguide.mvvm.ui.MvvmActivity
import com.example.microguide.refactor.step1.ui.Ref1Activity
import com.example.microguide.refactor.step2.ui.Ref2Activity
import com.example.microguide.refactor.step3.ui.Ref3Activity
import com.example.microguide.refactor.step4.ui.Ref4Activity
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
        ref1Button.setOnClickListener { Ref1Activity.start(this@MainActivity) }
        ref2Button.setOnClickListener { Ref2Activity.start(this@MainActivity) }
        ref3Button.setOnClickListener { Ref3Activity.start(this@MainActivity) }
        ref4Button.setOnClickListener { Ref4Activity.start(this@MainActivity) }
    }
}