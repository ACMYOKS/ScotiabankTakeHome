package com.acapp1412.scotiabanktakehome

import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("/users/{userId}")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @GET("/users/{userId}/repos")
    suspend fun getUserRepos(
        @Path("userId") userId: String,
        @Query("per_page") pageSize: Int = 100
    ): Response<List<Repo>>
}