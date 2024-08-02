package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.foodbite.models.AuthToken
import com.project.foodbite.network.apiServices
import com.project.foodbite.utils.logger
import kotlinx.coroutines.launch
import org.json.JSONObject

class VerifyCodeViewModel : ViewModel() {
    private val _verifyCodeResponse = MutableLiveData<AuthToken>()

    val verifyCodeResponse: LiveData<AuthToken>
        get() = _verifyCodeResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun verifyCode(code: Int, mobileNumber: String, hash: String, expiresAt: String) {
        val data = hashMapOf<String, Any>(
            "code" to code,
            "mobile_number" to mobileNumber,
            "hash" to hash,
            "expires_at" to expiresAt
        )

        viewModelScope.launch {
            try {
                val res = apiServices().verifyCode(data)
                if (res.isSuccessful && res.body() != null) {
                    _verifyCodeResponse.value = res.body()!!.data
                } else {
                    val errorBody = res.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    _errorMessage.value = jsonObject.getString("message")
                }
            } catch (throwable: Throwable) {
                logger(throwable.message.toString())
                throw throwable
            }
        }

    }
}