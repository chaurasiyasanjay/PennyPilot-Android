package com.pennypilot.app.data.repository

import com.pennypilot.app.data.mapper.toDomain
import com.pennypilot.app.data.remote.api.ExchangeRateApi
import com.pennypilot.app.domain.model.ExchangeRate
import com.pennypilot.app.domain.repository.ExchangeRateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRateRepositoryImpl @Inject constructor(
    private val exchangeRateApi: ExchangeRateApi
) : ExchangeRateRepository {

    override suspend fun getLatestRate(
        baseCurrency: String,
        quoteCurrency: String
    ): ExchangeRate = exchangeRateApi
        .getLatestRate(baseCurrency, quoteCurrency)
        .toDomain()
}
