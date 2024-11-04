package com.acapp1412.scotiabanktakehome.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Long,
    val login: String,
    val name: String?,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("public_repos")
    val publicRepos: Int,
) : Parcelable
