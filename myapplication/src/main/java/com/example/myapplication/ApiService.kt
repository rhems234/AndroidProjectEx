package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService  {
    @POST("travel/user/login")
    fun login(@Body request: LoginRequest): Call<User>

    @POST("travel/user/register")
    fun register(@Body request: User) : Call<User>
}
