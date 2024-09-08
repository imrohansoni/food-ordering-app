package com.project.foodbite.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.project.foodbite.controllers.LoginController
import com.project.foodbite.databinding.ActivityLoginBinding
import com.project.foodbite.ui.State
import com.project.foodbite.ui.components.LoadingDialog
import com.project.foodbite.utils.AppSignatureHelper
import com.project.foodbite.utils.logger
import com.project.foodbite.viewModels.LoginViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private val pattern = Regex("^\\d{10}\$")
    private lateinit var loadingDialog: LoadingDialog

    private val googleSignLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    loginController.handlerSignInResult(it)
                }

            }
        }

    private lateinit var loginController: LoginController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginController = LoginController(this, loginViewModel)

        window.statusBarColor = Color.BLACK

        binding.continueButton.disabled = true
        loadingDialog = LoadingDialog(this)
        logger(message = AppSignatureHelper(this).getAppSignatures()[0])

        binding.mobileNumberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lifecycleScope.launch {
                    showPhoneNumberSuggestion()
                }
            }
        }

        binding.mobileNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence, start: Int, before: Int, count: Int
            ) {
                binding.continueButton.disabled = !charSequence.matches(pattern)
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        binding.loginWithGoogleButton.setOnClickListener {
            val intent = loginController.loginWithGoogle()
            googleSignLauncher.launch(intent)
        }

        binding.continueButton.setOnClickListener {
            continueButtonHandler()
        }

        loginViewModel.sendCodeState.observe(this) { uiState ->
            when (uiState) {
                is State.Error -> {
                    binding.continueButton.stopLoading()
                    binding.mobileNumberEditText.isEnabled = true
                    Toast.makeText(this@LoginActivity, uiState.message, Toast.LENGTH_SHORT).show()
                }

                State.Loading -> {
                    binding.continueButton.startLoading("sending code")
                    binding.mobileNumberEditText.isEnabled = false
                    binding.continueButton.isEnabled = true
                }

                is State.Success -> {
                    Intent(this@LoginActivity, VerifyCodeActivity::class.java).apply {
                        putExtra("LOGIN_RESPONSE", uiState.data)
                        startActivity(this)
                        finish()
                        binding.continueButton.stopLoading()
                    }
                }
            }
        }

        loginViewModel.loginWithGoogleState.observe(this) { uiState ->
            when (uiState) {
                is State.Error -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this@LoginActivity, uiState.message, Toast.LENGTH_SHORT).show()
                }

                State.Loading -> {
                    loadingDialog.show()
                }

                is State.Success -> {
                    loadingDialog.show()
                    Intent(this@LoginActivity, LocationActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                }
            }
        }
    }

    private suspend fun showPhoneNumberSuggestion() {
        val hintRequest = GetPhoneNumberHintIntentRequest.builder().build()
        try {
            val pendingIntent =
                Identity.getSignInClient(this).getPhoneNumberHintIntent(hintRequest).await()
            val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
            phonePickIntentResultLauncher.launch(intentSenderRequest)
        } catch (ex: Exception) {
            Log.e("mobileNumberHint", "Phone Number Hint failed")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@LoginActivity, "read sms permission is granted", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@LoginActivity, "read sms permission denied", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun continueButtonHandler() {
        val mobileNumber = binding.mobileNumberEditText.text
        if (!mobileNumber.matches(pattern)) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_LONG).show()
            return
        }

        loginViewModel.sendCode(binding.mobileNumberEditText.text.toString())
    }

    private val phonePickIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            result?.let { activityResult ->
                val intent = activityResult.data
                val mobileNumber = intent?.getStringExtra("phone_number_hint_result")

                mobileNumber?.let {
                    if (it.startsWith("+91")) {
                        val mobile = it.replace("+91", "")
                        binding.mobileNumberEditText.setText(mobile)
                        binding.mobileNumberEditText.setSelection(mobile.length)
                    }
                }
            }
        }
}


