package com.example.myapplication

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private lateinit var api: ApiService

    val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.100.105.150:9080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    fun getApiService(): ApiService {
        return api
    }
}
