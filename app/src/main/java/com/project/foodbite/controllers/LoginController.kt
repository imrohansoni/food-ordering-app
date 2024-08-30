package com.project.foodbite.controllers

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.project.foodbite.R
import com.project.foodbite.utils.logger
import com.project.foodbite.viewModels.LoginViewModel
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class LoginController(
    activity: Activity,
    private val loginViewModel: LoginViewModel
) {
    private var googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(
        activity, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
    )

    private val auth = Firebase.auth

    private suspend fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        try {
            val user = auth.signInWithCredential(credential).await().user
            user?.let {
                loginViewModel.loginWithGoogle(it)
            }
        } catch (ex: Exception) {
            logger(message = ex.message.toString())
        }
    }

    fun loginWithGoogle(): Intent {
        val signInIntent = googleSignInClient.signInIntent
        return signInIntent
    }

    suspend fun handlerSignInResult(result: ActivityResult) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (exception: ApiException) {
            Log.d("FOODBITE_EXCEPTION", exception.message.toString())
        }
    }
}