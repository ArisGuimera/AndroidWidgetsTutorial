package com.aristidevs.widgetstutorial.quotewidget.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://twitch-f8384-default-rtdb.europe-west1.firebasedatabase.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val apiService = retrofit.create(QuoteApiService::class.java)

    suspend fun getRandomQuote(): String {
        val response = apiService.getQuotes()
        return response.quotes.randomOrNull() ?: "No quotes available."
    }
}