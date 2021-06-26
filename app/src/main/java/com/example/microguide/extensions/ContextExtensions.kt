package com.example.microguide.extensions

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

inline fun <reified A : AppCompatActivity> Context.startActivity() {
    startActivity(Intent(this, A::class.java))
}