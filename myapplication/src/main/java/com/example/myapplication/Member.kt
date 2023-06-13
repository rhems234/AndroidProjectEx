package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class Member(
    @SerializedName("id")
    val id: String,

    @SerializedName("pw")
    val pw: String,

    @SerializedName("nickname")
    val nickname: String
)