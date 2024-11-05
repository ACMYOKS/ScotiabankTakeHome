package com.acapp1412.scotiabanktakehome.data

data class UserWithRepo(val userId: String, val repos: List<Repo>) {
    val totalForks = repos.sumOf { it.forks }
}