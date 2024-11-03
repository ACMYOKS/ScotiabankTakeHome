package com.acapp1412.scotiabanktakehome

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun RetrofitBuilder(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}