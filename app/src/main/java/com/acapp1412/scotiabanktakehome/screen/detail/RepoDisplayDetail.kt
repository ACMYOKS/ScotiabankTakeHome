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
    /***
     * function to determine which color should be used for the sum of forks of all repos.
     * RED is the star badge color, which is used when the sum of forks of all repos exceeds 5000,
     * otherwise GRAY is used
     */
    @ColorInt
    fun getForkTextColor(): Int = if (totalForks > 5000) {
        Color.RED
    } else {
        Color.GRAY
    }
}