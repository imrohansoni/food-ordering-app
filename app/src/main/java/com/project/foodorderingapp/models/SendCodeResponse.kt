package com.project.foodorderingapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class SendCodeResponse(
    @SerializedName("expires_at")
    val expiresAt: String,
    val hash: String,
    @SerializedName("mobile_number")
    val mobileNumber: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(expiresAt)
        parcel.writeString(hash)
        parcel.writeString(mobileNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SendCodeResponse> {
        override fun createFromParcel(parcel: Parcel): SendCodeResponse {
            return SendCodeResponse(parcel)
        }

        override fun newArray(size: Int): Array<SendCodeResponse?> {
            return arrayOfNulls(size)
        }
    }
}