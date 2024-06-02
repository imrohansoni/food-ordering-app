package com.project.foodorderingapp.network

import com.project.foodorderingapp.models.ApiResponse
import com.project.foodorderingapp.models.AuthToken
import com.project.foodorderingapp.models.SendCodeResponse
import com.project.foodorderingapp.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ApiService {
    @POST("auth/send-code")
    suspend fun sendCode(
        @Body data: HashMap<String, String>
    ): Response<ApiResponse<SendCodeResponse>>

    @POST("auth/login")
    suspend fun login(
        @Body data: HashMap<String, Any>
    ): Response<ApiResponse<AuthToken>>

    @GET("account")
    suspend fun getAccount(): Response<ApiResponse<User>>

    @PATCH("account/name")
    suspend fun updateName(
        @Body data: HashMap<String, String>
    ): Response<ApiResponse<String>>

    @PATCH("account/location")
    suspend fun updateLocation(
        @Body data: HashMap<String, Double>
    ): Response<ApiResponse<String>>

    @PATCH("account/email")
    suspend fun updateEmail(
        @Body data: HashMap<String, String>
    ): Response<ApiResponse<String>>


    @POST("account/mobile/send-code")
    suspend fun updateMobileSendCode(
        @Body data: HashMap<String, Double>
    ): Response<ApiResponse<String>>


    @PATCH("account/mobile")
    suspend fun updateMobileVerifyCode(
        @Body data: HashMap<String, Double>
    ): Response<ApiResponse<String>>

}