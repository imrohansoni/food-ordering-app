package com.project.foodbite.ui.activities

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
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.foodbite.R
import com.project.foodbite.databinding.ActivityVerifyCodeBinding
import com.project.foodbite.models.LoginResponse
import com.project.foodbite.viewModels.VerifyCodeViewModel


class VerifyCodeActivity : AppCompatActivity() {
    private val verifyCodeViewModel: VerifyCodeViewModel by viewModels()
    private lateinit var binding: ActivityVerifyCodeBinding
    private var code = String()
    private var loginResponse: LoginResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)

        loginResponse = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable("LOGIN_RESPONSE", LoginResponse::class.java)
        } else {
            intent.extras?.getParcelable("LOGIN_RESPONSE") as? LoginResponse
        }


        binding.codeEditText1.isEnabled = true
        binding.codeEditText1.requestFocus()

        binding.codeEditText2.isEnabled = false
        binding.codeEditText3.isEnabled = false
        binding.codeEditText4.isEnabled = false


        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            showSoftInput(binding.codeEditText1, InputMethodManager.SHOW_IMPLICIT)
        }

        addInputListener(binding.codeEditText1)
        addInputListener(binding.codeEditText2)
        addInputListener(binding.codeEditText3)
        addInputListener(binding.codeEditText4)


        verifyCodeViewModel.verifyCodeResponse.observe(this) {
            Intent(this@VerifyCodeActivity, LocationActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }

        verifyCodeViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            binding.verifyCodeButton.stopLoading()
        }

        binding.verifyCodeButton.setOnClickListener {
            binding.verifyCodeButton.startLoading("wait, verifying code...")
            val intent = Intent(this, LocationActivity::class.java)
            startActivity(intent)
        }

        binding.verifyCodeButton.setOnClickListener(::verifyCodeButtonHandler)
        val sendCodeMessage = SpannableString(
            getString(
                R.string.send_code_message,
                binding.verificationScreenTextView.text,
                loginResponse?.mobileNumber
            )
        )

        sendCodeMessage.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            48,
            58,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.verificationScreenTextView.text = sendCodeMessage


        setupResendCodeTimer()
        toggleVerifyCodeButton()
    }

    private fun verifyCodeButtonHandler(view: View) {
        val mobileNumber = loginResponse!!.mobileNumber
        val expiresAt = loginResponse!!.expiresAt
        val hash = loginResponse!!.hash

        if (code.length == 4) {
            binding.verifyCodeButton.startLoading("wait, verifying code")
            try {
                verifyCodeViewModel.verifyCode(
                    code = Integer.parseInt(code), mobileNumber, hash, expiresAt
                )
            } catch (e: Exception) {
                Log.e("FOODBITE_LOGGER", e.toString())
            }

        } else {
            Toast.makeText(
                this,
                "please enter the 4 digit verification code ${code.length}",
                Toast.LENGTH_SHORT
            ).show()
        }


    }


    private fun unShiftTextViewFocus(editText: EditText) {
        val previous = when (editText.id) {
            binding.codeEditText2.id -> binding.codeEditText1
            binding.codeEditText3.id -> binding.codeEditText2
            binding.codeEditText4.id -> binding.codeEditText3
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
            binding.codeEditText1.id -> binding.codeEditText2
            binding.codeEditText2.id -> binding.codeEditText3
            binding.codeEditText3.id -> binding.codeEditText4
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
                binding.resendCodeTextView.text =
                    getString(R.string.resend_code_text, countdown)
            }

            override fun onFinish() {

            }
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
            binding.codeEditText1.text.toString() + binding.codeEditText2.text.toString() + binding.codeEditText3.text.toString() + binding.codeEditText4.text.toString()
        binding.verifyCodeButton.disabled = code.length != 4
    }

}