package com.sana.kotlinwithretrofit

import com.google.firebase.auth.FirebaseUser
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("website")
    var website: String,

    @SerializedName("login")
    var username: String,

    @SerializedName("type")
    var userType: String,

    @SerializedName("avatar_url")
    val image: String
) {
    companion object {
        fun fromFirebaseUser(firebaseUser: FirebaseUser): User {
            return User(
                website = "", // Set an appropriate value or keep it empty
                username = firebaseUser.displayName ?: "",
                userType = "psw", // Assuming "psw" is the user type for Firebase authentication
                image = firebaseUser.photoUrl?.toString() ?: ""
            )
        }
    }
}