package com.aristidevs.widgetstutorial.quotewidget.data

import com.aristidevs.widgetstutorial.quotewidget.data.response.QuoteResponse
import retrofit2.http.GET

interface QuoteApiService {
    @GET(".json")
    suspend fun getQuotes(): QuoteResponse
}