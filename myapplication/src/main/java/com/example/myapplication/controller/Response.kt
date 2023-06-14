package com.example.myapplication.controller

data class LoginResponse(
    val status: String,
    val message: String,
    val sessionId: String,
    val sessionPw: String,
    val nickname: String
)

data class RegisterResponse(val status: String, val message: String)

