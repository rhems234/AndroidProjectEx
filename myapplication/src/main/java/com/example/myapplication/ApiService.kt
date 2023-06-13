package com.example.myapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService  {
    @POST("test/insertMember")
    fun insertMember(
        @Query("id") id: String,
        @Query("pw") pw: String,
        @Query("nickname") nickname: String
    ): Call<String>

    @GET("/test/getMemberDetail")
    fun getMemberDetail(@Query("id") id: String): Call<Member>

    @POST("/test/updateMember")
    fun updateMember(
        @Query("id") id: String,
        @Query("pw") pw: String,
        @Query("nickname") nickname: String
    ): Call<String>

    @GET("/test/deleteMember")
    fun deleteMember(@Query("id") id: String): Call<String>
    abstract fun login(id: String, pw: String): Call<LoginResponse>

}
