package com.acapp1412.scotiabanktakehome.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    val name: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    @SerializedName("public_repos")
    val publicRepos: Int,
)
