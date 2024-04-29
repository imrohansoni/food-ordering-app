package com.project.foodorderingapp.ui.activity

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
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.project.foodorderingapp.api.apiServices
//import com.project.foodorderingapp.api.apiServices
import com.project.foodorderingapp.databinding.ActivityLoginBinding

import com.project.foodorderingapp.models.ApiResponse
import com.project.foodorderingapp.models.SendCodeResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
        data["mobileNumber"] = mobileNumber.toString()
        makeSendCodeRequest(data)
    }

    private fun makeSendCodeRequest(data: HashMap<String, String>) {
        apiServices().sendCode(data)
            .enqueue(object : Callback<ApiResponse<SendCodeResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<SendCodeResponse>>,
                    response: Response<ApiResponse<SendCodeResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Intent(applicationContext, VerifyCodeActivity::class.java).apply {
                            this.putExtra("sendCodeResponse", response.body()!!.data)
                            startActivity(this)
                        }

                        binding.continueButton.stopLoading()
                        binding.mobileNumberEditText.isEnabled = true
                    } else {
                        binding.continueButton.stopLoading()
                        val errorBody = response.errorBody()!!.string()
                        val jsonObject = JSONObject(errorBody.trim { it <= ' ' })
                        val error = jsonObject.getString("error")

                        Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<SendCodeResponse>>,
                    throwable: Throwable
                ) {
                    Toast.makeText(applicationContext, "something went wrong", Toast.LENGTH_SHORT).show()
                    binding.continueButton.stopLoading()
                    binding.mobileNumberEditText.isEnabled = true
                }
            })


//        Intent(applicationContext, VerifyCodeActivity::class.java).apply {
//            val body = HashMap<String, Any>()
//            body["mobileNumber"] = "8851138132"
//            body["expiresAt"] = "2024-04-27T16:15:28.286Z"
//            body["hash"] = "45188f812b80be8481f95adc81703627cf63bf345e29e5a8037069c03f01af32"
//
//            this.putExtra("sendCodeResponse", body)
//            startActivity(this)
//        }


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