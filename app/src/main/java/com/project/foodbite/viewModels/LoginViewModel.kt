package com.project.foodbite.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.project.foodbite.api.apiServices
import com.project.foodbite.models.AuthToken
import com.project.foodbite.models.SendCodeResponse
import com.project.foodbite.ui.State
import com.project.foodbite.utils.apiCall

class LoginViewModel : ViewModel() {
    private val _sendCodeState = MutableLiveData<State<SendCodeResponse>>()
    val sendCodeState: LiveData<State<SendCodeResponse>>
        get() = _sendCodeState

    private val _loginWithGoogleState = MutableLiveData<State<AuthToken>>()
    val loginWithGoogleState: LiveData<State<AuthToken>>
        get() = _loginWithGoogleState

    fun sendCode(mobileNumber: String) {
        val data = hashMapOf("mobile_number" to mobileNumber)
        apiCall(viewModelScope, _sendCodeState) {
            val res = apiServices().sendCode(data)
            return@apiCall res.data
        }
    }

    fun loginWithGoogle(user: FirebaseUser) {
        val firebaseUser = hashMapOf(
            "user_id" to user.uid,
            "first_name" to user.displayName?.split(" ")?.getOrNull(0),
            "last_name" to user.displayName?.split(" ")?.getOrNull(1),
            "email" to user.email,
            "mobile_number" to user.phoneNumber
        )
        apiCall(viewModelScope, _loginWithGoogleState) {
            val res = apiServices().loginWithGoogle(firebaseUser)
            return@apiCall res.data
        }
    }
}
