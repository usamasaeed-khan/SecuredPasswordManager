package com.example.PasswordManager

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//Singleton Class
object RetrofitInstance {

    private const val BASE_URL="https://www.facebook.com/.well-known/"



    val instance:APIService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(APIService::class.java)

    }



}