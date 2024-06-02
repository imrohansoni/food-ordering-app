package com.project.foodorderingapp.network

import android.content.Context
import com.project.foodorderingapp.utils.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor(private val context: Context, private val tokenRequired: Boolean) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var originalRequest = chain.request()
//        val originalURL = originalRequest.url

//        var newUrl = originalURL.newBuilder()

        if (tokenRequired) {
            AuthTokenManager.getAuthToken(context)?.let {
                originalRequest = originalRequest.newBuilder().addHeader("authorization", "Bearer $it").build()
//                newUrl = originalURL.newBuilder().addQueryParameter("authToken", it)
            }
        }

//        val requestBuilder = originalRequest.newBuilder().url(newUrl.build()).build()
        return chain.proceed(originalRequest)
    }
}