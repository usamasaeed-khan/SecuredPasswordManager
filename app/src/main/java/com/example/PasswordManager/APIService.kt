package com.example.PasswordManager

import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET("assetlinks.json")
    fun GetCategories():Call<String>

}