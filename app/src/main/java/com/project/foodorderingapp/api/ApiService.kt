package com.project.foodorderingapp.api

import com.project.foodorderingapp.models.ApiResponse
import com.project.foodorderingapp.models.AuthToken
import com.project.foodorderingapp.models.SendCodeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/send-code")
    fun sendCode(
        @Body data: HashMap<String, String>
    ) : Call<ApiResponse<SendCodeResponse>>


    @POST("auth/verify-mobile")
    fun login(
        @Body data: HashMap<String, Any>
    ) : Call<ApiResponse<AuthToken>>
}