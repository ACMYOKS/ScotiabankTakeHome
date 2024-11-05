package com.acapp1412.scotiabanktakehome.screen.detail

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import com.acapp1412.scotiabanktakehome.data.Repo
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepoDisplayDetail(
    val userId: String,
    val repo: Repo,
    val totalForks: Int
) : Parcelable {
    @ColorInt fun getForkTextColor(): Int = if (totalForks > 5000) {
        Color.RED
    } else {
        Color.GRAY
    }
}