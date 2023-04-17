package com.sana.kotlinwithretrofit

import com.google.gson.annotations.SerializedName

data class User(

    @SerializedName("website")
    var website: String,

    @SerializedName("login")
    var username: String,

    //The user type is psw
    @SerializedName("type")
    var userType: String,
    //

    @SerializedName("avatar_url")
    val image: String
)