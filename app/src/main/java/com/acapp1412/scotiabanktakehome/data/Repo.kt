package com.acapp1412.scotiabanktakehome.data

import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Long,
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    val description: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Int,
    val fork: Boolean,
    val forks: Int
)