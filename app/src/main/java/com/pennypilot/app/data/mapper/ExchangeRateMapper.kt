package com.pennypilot.app.data.mapper

import com.pennypilot.app.data.remote.dto.ExchangeRateDto
import com.pennypilot.app.domain.model.ExchangeRate

fun ExchangeRateDto.toDomain(): ExchangeRate = ExchangeRate(
    baseCurrency = base,
    quoteCurrency = quote,
    rate = rate,
    date = date
)
