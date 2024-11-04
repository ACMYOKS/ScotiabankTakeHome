package com.acapp1412.scotiabanktakehome.screen.detail

import android.os.Parcelable
import com.acapp1412.scotiabanktakehome.data.Repo
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepoDisplayDetail(
    val userId: String,
    val repo: Repo
) : Parcelable