package com.project.foodbite.ui.activities


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.project.foodbite.databinding.ActivityLoginBinding
import com.project.foodbite.viewModels.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding
    private val pattern = Regex("^\\d{10}\$")

    private val phonePickIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                val intent = result.data
                var mobileNumber = intent!!.getStringExtra("phone_number_hint_result")
                if (mobileNumber != null) {
                    if (mobileNumber.startsWith("+91")) {
                        mobileNumber = mobileNumber.replace("+91", "")
                    }
                    binding.mobileNumberEditText.setText(mobileNumber)
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.BLACK

        showPhoneNumberSuggestion()

        binding.continueButton.disabled = true

        binding.mobileNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                binding.continueButton.disabled = !charSequence.matches(pattern)
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        binding.continueButton.setOnClickListener(::continueButtonHandler)

        loginViewModel.loginResponse.observe(this) { loginResponse ->
            Intent(this@LoginActivity, VerifyCodeActivity::class.java).apply {
                putExtra("LOGIN_RESPONSE", loginResponse)
                startActivity(this)
                finish()
            }
        }

        loginViewModel.errorMessage.observe(this) {
            Toast.makeText(this@LoginActivity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun continueButtonHandler(view: View) {
        val mobileNumber = binding.mobileNumberEditText.text
        if (!mobileNumber.matches(pattern)) {
            Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_LONG).show()
            return
        }

        binding.continueButton.startLoading("wait, sending code...")

        binding.mobileNumberEditText.isEnabled = false
        binding.continueButton.isEnabled = true
        loginViewModel.login(mobileNumber = binding.mobileNumberEditText.text.toString())
    }


    private fun showPhoneNumberSuggestion() {
        val hintRequest = GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(this)
            .getPhoneNumberHintIntent(hintRequest)
            .addOnSuccessListener {
                try {
                    val request = IntentSenderRequest.Builder(it.intentSender).build()
                    phonePickIntentResultLauncher.launch(request)
                } catch (e: Exception) {
                    Log.e("mobileNumberHint", "Launching the PendingIntent failed")
                }
            }.addOnFailureListener {
                Log.e("mobileNumberHint", "Phone Number Hint failed")
            }
    }

}


