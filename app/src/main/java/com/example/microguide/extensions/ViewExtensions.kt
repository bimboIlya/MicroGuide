package com.example.microguide.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar

fun View.showSnackBar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_SHORT)
        .setAction("Dismiss") { }
        .show()
}

fun View.showSnackBar() {
    showSnackBar("poop lole")
}

inline var View.isDisabled: Boolean
    get() = !isEnabled
    set(value) {
        isEnabled = !value
    }

fun Activity.hideKeyboard() {
    val imm = getSystemService<InputMethodManager>()
    val currentFocus = currentFocus

    if (imm != null && currentFocus != null) {
        imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
    }
}