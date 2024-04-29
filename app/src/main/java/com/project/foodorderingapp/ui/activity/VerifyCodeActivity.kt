package com.project.foodorderingapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.foodorderingapp.R
import com.project.foodorderingapp.api.apiServices
import com.project.foodorderingapp.databinding.ActivityVerifyCodeBinding
import com.project.foodorderingapp.models.ApiResponse
import com.project.foodorderingapp.models.AuthToken
import com.project.foodorderingapp.models.SendCodeResponse
import com.project.foodorderingapp.utils.AuthTokenManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VerifyCodeActivity : AppCompatActivity() {
    private lateinit var b: ActivityVerifyCodeBinding
    private var code = String()
    private var sendCodeResponse: SendCodeResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(b.root)

        sendCodeResponse = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable("sendCodeResponse", SendCodeResponse::class.java)
        } else {
            intent.extras?.getParcelable("sendCodeResponse") as? SendCodeResponse
        }

        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)


        b.codeEditText1.isEnabled = true
        b.codeEditText1.requestFocus()

        b.codeEditText2.isEnabled = false
        b.codeEditText3.isEnabled = false
        b.codeEditText4.isEnabled = false


        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            showSoftInput(b.codeEditText1, InputMethodManager.SHOW_IMPLICIT)
        }

        addInputListener(b.codeEditText1)
        addInputListener(b.codeEditText2)
        addInputListener(b.codeEditText3)
        addInputListener(b.codeEditText4)

        b.verifyCodeButton.setOnClickListener {
            b.verifyCodeButton.startLoading("wait, verifying code...")
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        b.verifyCodeButton.setOnClickListener(::verifyCodeButtonHandler)
        val sendCodeMessage = SpannableString(getString(
            R.string.send_code_message,
            b.verificationScreenTextView.text,
            sendCodeResponse?.mobileNumber
        ))
        sendCodeMessage.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        b.verificationScreenTextView.text = sendCodeMessage


        setupResendCodeTimer()
        toggleVerifyCodeButton()
    }

    private fun verifyCodeButtonHandler(view: View) {
        if (code.length == 4) {
            b.verifyCodeButton.startLoading("wait, verifying code")

            val body = hashMapOf<String, Any>(
                "mobileNumber" to sendCodeResponse!!.mobileNumber,
                "hash" to sendCodeResponse!!.hash,
                "expiresAt" to sendCodeResponse!!.expiresAt,
                "code" to Integer.parseInt(code)
            )

            makeVerifyCodeRequest(body)
        } else {
            Toast.makeText(
                applicationContext,
                "please enter the 4 digit verification code ${code.length}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun makeVerifyCodeRequest(body: HashMap<String, Any>) {
        apiServices().login(body).enqueue(object : Callback<ApiResponse<AuthToken>> {
            override fun onResponse(
                call: Call<ApiResponse<AuthToken>>,
                response: Response<ApiResponse<AuthToken>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    AuthTokenManager.saveAuthToken(
                        applicationContext,
                        response.body()!!.data.authToken
                    )
                    Intent(applicationContext, LocationActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                } else {
                    b.verifyCodeButton.stopLoading()
                    val errorBody = response.errorBody()!!.string()
                    val jsonObject = JSONObject(errorBody.trim { it <= ' ' })
                    val error = jsonObject.getString("error")

                    Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT)
                        .show()
                }

            }

            override fun onFailure(p0: Call<ApiResponse<AuthToken>>, p1: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "something went wrong",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        })
    }

    private fun unShiftTextViewFocus(editText: EditText) {
        val previous = when (editText.id) {
            b.codeEditText2.id -> b.codeEditText1
            b.codeEditText3.id -> b.codeEditText2
            b.codeEditText4.id -> b.codeEditText3
            else -> null
        }

        previous?.let {
            it.isEnabled = true
            it.requestFocus()
            editText.isEnabled = false
        }
    }

    private fun shiftTextViewFocus(editText: EditText) {
        val nextEditText = when (editText.id) {
            b.codeEditText1.id -> b.codeEditText2
            b.codeEditText2.id -> b.codeEditText3
            b.codeEditText3.id -> b.codeEditText4
            else -> null
        }

        nextEditText?.let {
            it.isEnabled = true
            it.requestFocus()
            editText.isEnabled = false
        }
    }

    private fun setupResendCodeTimer() {
        object : CountDownTimer(180000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val countdown = String.format("%02d:%02d", minutes, seconds)
                b.resendCodeTextView.text =
                    getString(R.string.resend_code_text, countdown)
            }

            override fun onFinish() {}
        }.start()
    }

    private fun addInputListener(editText: EditText) {
        editText.setOnKeyListener { _, keyCode, event ->
            // this function will be called on every keystore on the edit text
            if (editText.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                // when user will press the delete button
                unShiftTextViewFocus(editText)
            }

            if (editText.text.isNotEmpty() && keyCode in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 && event.action == KeyEvent.ACTION_DOWN) {
                val number = keyCode - KeyEvent.KEYCODE_0
                editText.setText(number.toString())
                editText.setSelection(editText.text.length)
            }

            return@setOnKeyListener false
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // when user will type something in the input then input will become non-empty only then it will move to the next input
                if (s.isNotEmpty()) {
                    shiftTextViewFocus(editText)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                toggleVerifyCodeButton()
            }
        })
    }

    private fun toggleVerifyCodeButton() {
        code =
            b.codeEditText1.text.toString() + b.codeEditText2.text.toString() + b.codeEditText2.text.toString() + b.codeEditText4.text.toString()
        b.verifyCodeButton.disabled = code.length != 4
    }
}