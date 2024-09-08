package com.project.foodbite.ui.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.project.foodbite.R
import com.project.foodbite.databinding.ActivityVerifyCodeBinding
import com.project.foodbite.models.SendCodeResponse
import com.project.foodbite.ui.State
import com.project.foodbite.ui.components.LoadingDialog
import com.project.foodbite.utils.AuthTokenManager
import com.project.foodbite.utils.SmsRetrieverReceiver
import com.project.foodbite.utils.logger
import com.project.foodbite.viewModels.LoginViewModel
import com.project.foodbite.viewModels.VerifyCodeViewModel
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class VerifyCodeActivity : AppCompatActivity() {
    private val verifyCodeViewModel: VerifyCodeViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityVerifyCodeBinding
    private lateinit var loadingDialog: LoadingDialog
    private var sendCodeResponse: SendCodeResponse? = null
    private var resendCodeTimer: CountDownTimer? = null
    private val resendCodeTime = 30_000

    private val smsReceiver = SmsRetrieverReceiver {
        val otpPattern = Pattern.compile("\\d{4}")
        val matcher = otpPattern.matcher(it)
        if (matcher.find()) {
            val smsCode = matcher.group(0) ?: ""
            if (smsCode.length == 4) {
                binding.codeEditText1.setText(smsCode[0].toString())
                binding.codeEditText2.setText(smsCode[1].toString())
                binding.codeEditText3.setText(smsCode[2].toString())
                binding.codeEditText4.setText(smsCode[3].toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SmsRetriever.getClient(this).startSmsRetriever()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)

        sendCodeResponse = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getParcelable("LOGIN_RESPONSE", SendCodeResponse::class.java)
        } else {
            intent.extras?.getParcelable("LOGIN_RESPONSE") as? SendCodeResponse
        }

        binding.mobileNumberTextView.text = sendCodeResponse?.mobileNumber

        loadingDialog = LoadingDialog(this)
        loadingDialog.loadingText = "sending code"

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

        loginViewModel.sendCodeState.observe(this) { state ->
            when (state) {
                is State.Error -> {
                    loadingDialog.dismiss()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }

                State.Loading -> {
                    loadingDialog.show()
                }

                is State.Success -> {
                    startResendCodeTimer()
                    loadingDialog.dismiss()
                    sendCodeResponse = state.data
                }
            }
        }

        verifyCodeViewModel.verifyCodeState.observe(this) { state ->
            when (state) {
                is State.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.verifyCodeButton.stopLoading()
                    binding.codeEditText4.isEnabled = true
                }

                State.Loading -> {
                    binding.verifyCodeButton.startLoading("wait, verifying code...")
                    binding.codeEditText4.isEnabled = false
                }

                is State.Success -> {
                    binding.verifyCodeButton.stopLoading()
                    AuthTokenManager.saveAuthToken(applicationContext, state.data.authToken)
                    Intent(this@VerifyCodeActivity, LocationActivity::class.java).apply {
                        startActivity(this)
                        finishAffinity()
                    }
                }
            }
        }

        binding.verifyCodeButton.setOnClickListener {
            verifyCode()
        }

        binding.sendAgainButton.setOnClickListener {
            loginViewModel.sendCode(sendCodeResponse!!.mobileNumber)
        }

        startResendCodeTimer()
        toggleVerifyCodeButton()
    }

    private fun verifyCode() {
        val mobileNumber = sendCodeResponse!!.mobileNumber
        val expiresAt = sendCodeResponse!!.expiresAt
        val hash = sendCodeResponse!!.hash

        val code =
            binding.codeEditText1.text.toString() + binding.codeEditText2.text.toString() + binding.codeEditText3.text.toString() + binding.codeEditText4.text.toString()

        if (code.length == 4) {
            binding.verifyCodeButton.startLoading("verifying code")
            try {
                verifyCodeViewModel.verifyCode(Integer.parseInt(code), mobileNumber, hash, expiresAt)
            } catch (e: Exception) {
                logger(e.toString())
            }
        }
    }

    private fun focusPreviousEditText(editText: EditText) {
        val previous = when (editText.id) {
            binding.codeEditText2.id -> binding.codeEditText1
            binding.codeEditText3.id -> binding.codeEditText2
            binding.codeEditText4.id -> binding.codeEditText3
            else -> null
        }

        previous?.let {
            it.isEnabled = true
            it.requestFocus()
            it.text.clear()
            editText.isEnabled = false
        }
    }

    private fun focusNextEditText(editText: EditText) {
        val nextEditText = when (editText.id) {
            binding.codeEditText1.id -> binding.codeEditText2
            binding.codeEditText2.id -> binding.codeEditText3
            binding.codeEditText3.id -> binding.codeEditText4
            else -> {
                verifyCode()
                null
            }
        }

        nextEditText?.let {
            it.isEnabled = true
            it.requestFocus()
            editText.isEnabled = false
        }
    }

    private fun startResendCodeTimer() {
        binding.sendAgainButton.visibility = View.GONE
        binding.countdownTextView.visibility = View.VISIBLE
        binding.resendCodeTextView.setText(R.string.resend_code_text_1)
        resendCodeTimer?.cancel()

        resendCodeTimer = object : CountDownTimer(resendCodeTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val countdown =
                    "$minutes".padStart(2, '0') + ":" + "$seconds".padStart(2, '0') + " seconds"
                binding.countdownTextView.text = countdown
            }

            override fun onFinish() {
                binding.resendCodeTextView.text = getString(R.string.resend_code_text_2)
                binding.countdownTextView.visibility = View.GONE
                binding.sendAgainButton.visibility = View.VISIBLE
            }

        }.start()
    }

    private fun addInputListener(editText: EditText) {
        editText.setOnKeyListener { _, keyCode, event ->
            if (editText.text.isEmpty() && keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                focusPreviousEditText(editText)
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
                if (s.length == 4) {
                    binding.codeEditText1.setText(s[0].toString())
                    binding.codeEditText2.setText(s[1].toString())
                    binding.codeEditText3.setText(s[2].toString())
                    binding.codeEditText4.setText(s[3].toString())
                } else if (s.isNotEmpty()) {
                    focusNextEditText(editText)
                    editText.setSelection(1)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                toggleVerifyCodeButton()
            }
        })
    }

    private fun toggleVerifyCodeButton() {
        val code =
            binding.codeEditText1.text.toString() + binding.codeEditText2.text.toString() + binding.codeEditText3.text.toString() + binding.codeEditText4.text.toString()
        binding.verifyCodeButton.disabled = code.length != 4
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsReceiver)
    }
}