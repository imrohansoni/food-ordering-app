package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.project.foodbite.models.AuthToken
import com.project.foodbite.models.LoginResponse
import com.project.foodbite.network.apiServices
import com.project.foodbite.ui.State
import com.project.foodbite.utils.logger
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val _loginWithMobileState = MutableLiveData<State<LoginResponse>>()
    val loginWithMobileState: LiveData<State<LoginResponse>>
        get() = _loginWithMobileState

    private val _loginWithGoogleState = MutableLiveData<State<AuthToken>>()
    val loginWithGoogleState: LiveData<State<AuthToken>>
        get() = _loginWithGoogleState

    fun loginWithMobile(mobileNumber: String) {
        _loginWithMobileState.value = State.Loading
        val data = hashMapOf("mobile_number" to mobileNumber)
        viewModelScope.launch {
            try {
                val res = apiServices().login(data)
                if (res.isSuccessful) {
                    res.body()?.let { response ->
                        _loginWithMobileState.value = State.Success(response.data)
                    } ?: run {
                        _loginWithMobileState.value = State.Error("Empty response body")
                    }
                } else {
                    val errorBody = res.errorBody()?.string()
                    val message = errorBody?.let {
                        val jsonObject = JSONObject(it)
                        jsonObject.getString("message")
                    } ?: "Unknown error"
                    _loginWithMobileState.value = State.Error(message)
                }
            } catch (throwable: Throwable) {
                logger(message = throwable.message.toString())
                _loginWithMobileState.value = State.Error(throwable.message ?: "Unknown error")
            }
        }
    }

    fun loginWithGoogle(user: FirebaseUser) {
        _loginWithGoogleState.value = State.Loading
        val firebaseUser = hashMapOf(
            "user_id" to user.uid,
            "first_name" to user.displayName?.split(" ")?.getOrNull(0),
            "last_name" to user.displayName?.split(" ")?.getOrNull(1),
            "email" to user.email,
            "mobile_number" to user.phoneNumber
        )
        viewModelScope.launch {
            try {
                val res = apiServices().loginWithGoogle(firebaseUser)
                if (res.isSuccessful) {
                    res.body()?.let { response ->
                        _loginWithGoogleState.value = State.Success(response.data)
                    } ?: run {
                        _loginWithGoogleState.value = State.Error("Empty response body")
                    }
                } else {
                    val errorBody = res.errorBody()?.string()
                    val message = errorBody?.let {
                        val jsonObject = JSONObject(it)
                        jsonObject.getString("message")
                    } ?: "Unknown error"
                    _loginWithGoogleState.value = State.Error(message)
                }
            } catch (throwable: Throwable) {
                logger(throwable.message.toString())
                _loginWithGoogleState.value = State.Error(throwable.message ?: "Unknown error")
            }
        }
    }
}
