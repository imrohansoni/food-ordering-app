package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.foodbite.api.apiServices
import com.project.foodbite.models.AuthToken
import com.project.foodbite.ui.State
import com.project.foodbite.utils.apiCall

class VerifyCodeViewModel : ViewModel() {

    private val _verifyCodeState = MutableLiveData<State<AuthToken>>()
    val verifyCodeState: LiveData<State<AuthToken>>
        get() = _verifyCodeState

    fun verifyCode(code: Int, mobileNumber: String, hash: String, expiresAt: String) {

        val data = hashMapOf<String, Any>(
            "code" to code,
            "mobile_number" to mobileNumber,
            "hash" to hash,
            "expires_at" to expiresAt
        )
        apiCall(viewModelScope, _verifyCodeState) {
            val res = apiServices().verifyCode(data)
            return@apiCall res.data
        }
    }
}