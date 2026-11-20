package com.pennypilot.app.domain.model

/**
 * Exchange rate published for one currency pair.
 * The rate represents one unit of [baseCurrency] in [quoteCurrency].
 */
data class ExchangeRate(
    val baseCurrency: String,
    val quoteCurrency: String,
    val rate: Double,
    val date: String
)
