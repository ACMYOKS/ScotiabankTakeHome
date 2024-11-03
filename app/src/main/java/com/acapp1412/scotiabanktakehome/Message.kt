package com.acapp1412.scotiabanktakehome

import android.content.Context
import androidx.annotation.StringRes

sealed interface Message {
    fun toString(context: Context): String
    data class OfResource(@StringRes val resId: Int) : Message {
        override fun toString(context: Context): String {
            return context.getString(resId)
        }
    }
    data class OfString(val string: String) : Message {
        override fun toString(context: Context): String {
            return string
        }
    }
}