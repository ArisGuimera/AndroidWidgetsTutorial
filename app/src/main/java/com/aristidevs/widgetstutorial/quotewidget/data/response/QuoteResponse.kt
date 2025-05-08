package com.aristidevs.widgetstutorial.quotewidget.data.response

import com.google.gson.annotations.SerializedName

data class QuoteResponse(@SerializedName("frases") val quotes: List<String>)