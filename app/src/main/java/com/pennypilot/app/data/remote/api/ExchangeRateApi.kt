package com.pennypilot.app.data.remote.api

import com.pennypilot.app.data.remote.dto.ExchangeRateDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {
    @GET("rate/{base}/{quote}")
    suspend fun getLatestRate(
        @Path("base") baseCurrency: String,
        @Path("quote") quoteCurrency: String
    ): ExchangeRateDto
}
