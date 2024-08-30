package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.foodbite.models.AuthToken
import com.project.foodbite.network.apiServices
import com.project.foodbite.ui.State
import com.project.foodbite.utils.logger
import kotlinx.coroutines.launch
import org.json.JSONObject

class VerifyCodeViewModel : ViewModel() {

    private val _verifyCodeState = MutableLiveData<State<AuthToken>>()
    val verifyCodeState: LiveData<State<AuthToken>>
        get() = _verifyCodeState

    fun verifyCode(code: Int, mobileNumber: String, hash: String, expiresAt: String) {
        _verifyCodeState.value = State.Loading

        val data = hashMapOf<String, Any>(
            "code" to code,
            "mobile_number" to mobileNumber,
            "hash" to hash,
            "expires_at" to expiresAt
        )

        viewModelScope.launch {
            try {
                val res = apiServices().verifyCode(data)
                if (res.isSuccessful) {
                    res.body()?.let { resBody ->
                        _verifyCodeState.value = State.Success(resBody.data)
                    } ?: run {
                        _verifyCodeState.value = State.Error("Empty response body")
                    }
                } else {
                    val errorBody = res.errorBody()?.string()

                    val message = errorBody?.let {
                        val jsonObject = JSONObject(it)
                        jsonObject.getString("message")
                    } ?: "Unknown error"

                    _verifyCodeState.value = State.Error(message)
                }
            } catch (throwable: Throwable) {
                logger(throwable.message.toString())
                _verifyCodeState.value = State.Error(throwable.message.toString())
            }
        }

    }
}