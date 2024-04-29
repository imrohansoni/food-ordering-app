package com.project.foodorderingapp.models

data class ApiResponse<T>(
    val data: T,
    val status: String
)