package com.project.foodorderingapp.ui.activities

//import com.project.foodorderingapp.api.apiServices

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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.project.foodorderingapp.network.apiServices
import com.project.foodorderingapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val pattern = Regex("^\\d{10}\$")

    private val phonePickIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result != null) {
                val intent = result.data
                var phoneNumber = intent!!.getStringExtra("phone_number_hint_result")
                if (phoneNumber != null) {
                    if (phoneNumber.startsWith("+91")) {
                        phoneNumber = phoneNumber.replace("+91", "")
                    }
                    binding.mobileNumberEditText.setText(phoneNumber)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = Color.BLACK

        // when the login activity loads open the mobile number suggestion
//        binding.mobileNumberEditText.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) showPhoneNumberSuggestion()
//        }

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

        val data = HashMap<String, String>()
        data["mobile_number"] = mobileNumber.toString()
        makeSendCodeRequest(data)
    }

    private fun makeSendCodeRequest(data: HashMap<String, String>) {
        lifecycleScope.launch {
            try {
                val res =
                    apiServices(context = applicationContext, tokenRequired = false)
                        .sendCode(data)
                if (res.isSuccessful && res.body() != null) {
                    Intent(applicationContext, VerifyCodeActivity::class.java).apply {
                        putExtra("sendCodeResponse", res.body()!!.data)
                        startActivity(this)
                    }
                } else {
                    val errorBody = res.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody)
                    val message = jsonObject.getString("message")

                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (throwable: Throwable) {
                Toast.makeText(applicationContext, throwable.message, Toast.LENGTH_SHORT)
                    .show()
            } finally {
                binding.continueButton.stopLoading()
                binding.mobileNumberEditText.isEnabled = true
            }
        }
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