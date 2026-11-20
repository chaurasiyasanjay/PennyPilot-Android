package com.pennypilot.app.domain.usecase

import com.pennypilot.app.domain.model.ExchangeRate
import com.pennypilot.app.domain.repository.ExchangeRateRepository
import javax.inject.Inject

class GetExchangeRateUseCase @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository
) {
    suspend operator fun invoke(baseCurrency: String, quoteCurrency: String): ExchangeRate =
        exchangeRateRepository.getLatestRate(baseCurrency, quoteCurrency)
}
