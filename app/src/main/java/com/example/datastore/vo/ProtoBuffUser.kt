package com.example.datastore.vo

import android.os.Parcel
import android.os.Parcelable

data class ProtoBuffUser(
    override val username: String,
    override val password: String,
    override val message: Int,
    val index: Int
) : User {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun updateUser(newPassword: String, newMessage: Int): User {
        return this.copy(password = newPassword, message = newMessage)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // super.writeToParcel(parcel, flags)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeInt(message)
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProtoBuffUser> {
        override fun createFromParcel(parcel: Parcel): ProtoBuffUser {
            return ProtoBuffUser(parcel)
        }

        override fun newArray(size: Int): Array<ProtoBuffUser?> {
            return arrayOfNulls(size)
        }
    }
}