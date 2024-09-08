package com.project.foodbite.network

import com.project.foodbite.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun apiServices(): ApiService {
    // create the OkHttpClient and add the interceptor that will add headers and queries to all parameter
//    val okHttpClient = OkHttpClient()
//        .newBuilder()
//        .addInterceptor(ApiInterceptor(context, tokenRequired))
//        .build()
    // create a retrofit build add the okHttpClient, base URL
    val retrofit = Retrofit
        .Builder()
//        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()

    return retrofit.create(ApiService::class.java)
}