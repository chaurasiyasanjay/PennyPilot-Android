package com.pennypilot.app.domain.repository

import com.pennypilot.app.domain.model.ExchangeRate

interface ExchangeRateRepository {
    suspend fun getLatestRate(baseCurrency: String, quoteCurrency: String): ExchangeRate
}
