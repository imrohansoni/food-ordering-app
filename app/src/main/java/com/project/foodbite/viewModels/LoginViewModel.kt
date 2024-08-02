package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.foodbite.models.LoginResponse
import com.project.foodbite.network.apiServices
import com.project.foodbite.utils.logger
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse>
        get() = _loginResponse
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    fun login(mobileNumber: String) {
        val data = hashMapOf(
            "mobile_number" to mobileNumber
        )
        viewModelScope.launch {
            try {
                val res = apiServices().login(data)

                if (res.isSuccessful && res.body() != null) {
                    _loginResponse.value = res.body()!!.data
                } else {
                    val errorBody = res.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    _errorMessage.value = jsonObject.getString("message")
                }
            } catch (throwable: Throwable) {
                logger(message = throwable.message.toString())
                throw throwable
            }
        }
    }
}