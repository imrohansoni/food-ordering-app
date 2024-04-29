package com.project.foodorderingapp.api

import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // headers can added on the request because it being sent with all request
        // originalRequest.newBuilder().addHeader("token", Constant.TOKEN).build()
        // build method will return the Request object
        val originalRequest = chain.request()
        // queries can we added on the url
        val originalURL = originalRequest.url

        val newUrl = originalURL.newBuilder()
            .build()

//        val newUrl = originalURL.newBuilder().addQueryParameter("apiKey", .API_KEY)
//        .build()
        val requestBuilder = originalRequest.newBuilder().url(newUrl).build()

        return chain.proceed(requestBuilder)
    }
}