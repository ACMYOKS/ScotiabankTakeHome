package com.acapp1412.scotiabanktakehome

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import retrofit2.Retrofit

/**
 * Custom Application implementation to initialize instance of Retrofit, GithubService and
 * ImageLoader
 * */
class BaseApplication : Application(), SingletonImageLoader.Factory {
    private lateinit var retrofit: Retrofit
    private lateinit var _githubService: GithubService
    val githubService
        get() = _githubService

    override fun onCreate() {
        super.onCreate()
        retrofit = RetrofitBuilder("https://api.github.com")
        _githubService = retrofit.create(GithubService::class.java)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}