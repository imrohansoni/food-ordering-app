package com.project.foodbite.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.project.foodbite.ui.State
import com.project.foodbite.utils.Constants.TIMEOUT_DURATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import retrofit2.HttpException

fun checkPermission(context: Context, permission: String): Boolean {
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun logger(tag: String = "FOODBITE_LOGGER", message: String = "this is the logger") {
    Log.d(tag, message)
}

fun getErrorMessage(exception: HttpException): String {
    val errorBody = exception.response()?.errorBody()?.string()
    val message = errorBody?.let {
        val jsonObject = JSONObject(it)
        jsonObject.getString("message")
    } ?: "Unknown error"
    return message
}

fun <T> handleException(exception: Exception, state: MutableLiveData<State<T>>) {
    when (exception) {
        is HttpException -> {
            state.value = State.Error(getErrorMessage(exception))
        }

        is TimeoutCancellationException -> {
            state.value = State.Error("timeout, please try again")
        }

        else -> {
            state.value = State.Error("something went wrong")
        }
    }

    logger(message = exception.message.toString())
}

fun <T> apiCall(
    scope: CoroutineScope,
    liveData: MutableLiveData<State<T>>,
    callback: suspend () -> T
) {
    liveData.value = State.Loading
    scope.launch {
        try {
            withTimeout(TIMEOUT_DURATION) {
                val data = callback()
                liveData.value = State.Success(data)
            }
        } catch (exception: Exception) {
            handleException(exception, liveData)
        }
    }
}

