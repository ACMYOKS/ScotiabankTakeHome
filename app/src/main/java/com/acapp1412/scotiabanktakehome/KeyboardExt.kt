package com.acapp1412.scotiabanktakehome

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService

fun View.hideKeyboard() {
    val imm = getSystemService(context, InputMethodManager::class.java)
    imm?.hideSoftInputFromWindow(windowToken, 0)
}