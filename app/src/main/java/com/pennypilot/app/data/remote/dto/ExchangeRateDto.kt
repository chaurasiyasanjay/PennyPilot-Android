package com.pennypilot.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.pennypilot.app.domain.model.ExchangeRate

data class ExchangeRateDto(
    @SerializedName("date") val date: String,
    @SerializedName("base") val base: String,
    @SerializedName("quote") val quote: String,
    @SerializedName("rate") val rate: Double
)

fun ExchangeRateDto.toDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = base,
    quoteCurrency = quote,
    rate = rate,
    date = date
)
