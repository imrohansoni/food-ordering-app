package com.project.foodorderingapp.models

import com.google.gson.annotations.SerializedName

data class AuthToken(
    @SerializedName("auth_token")
    val authToken: String
)