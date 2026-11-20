package com.pennypilot.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennypilot.app.domain.model.Category
import com.pennypilot.app.domain.model.ExchangeRate
import com.pennypilot.app.domain.model.Transaction
import com.pennypilot.app.domain.repository.CategoryRepository
import com.pennypilot.app.domain.repository.SettingsRepository
import com.pennypilot.app.domain.repository.TransactionRepository
import com.pennypilot.app.domain.usecase.CalculateFinancialSummaryUseCase
import com.pennypilot.app.domain.usecase.GetExchangeRateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class ExchangeRateUiState(
    val rate: ExchangeRate? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    settingsRepository: SettingsRepository,
    private val getExchangeRate: GetExchangeRateUseCase,
    private val calculateFinancialSummary: CalculateFinancialSummaryUseCase
) : ViewModel() {

    private val selectedCategoryId = MutableStateFlow<Long?>(null)
    private val exchangeRateState = MutableStateFlow(ExchangeRateUiState())

    init {
        viewModelScope.launch {
            settingsRepository.observeSettings().collectLatest { settings ->
                val currency = settings.displayCurrency
                if (currency == "MYR") {
                    exchangeRateState.value = ExchangeRateUiState()
                } else {
                    exchangeRateState.value = ExchangeRateUiState(isLoading = true)
                    try {
                        val rate = getExchangeRate("MYR", currency)
                        exchangeRateState.value = ExchangeRateUiState(rate = rate)
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (exception: Exception) {
                        LOGGER.warning("Unable to retrieve exchange rate; using MYR fallback" + ": " + exception.message)
                        exchangeRateState.value = ExchangeRateUiState(
                            errorMessage = "Exchange rate unavailable. Showing amounts in MYR."
                        )
                    }
                }
            }
        }
    }

    val uiState = combine(
        transactionRepository.observeTransactions(),
        categoryRepository.observeCategories(),
        selectedCategoryId,
        settingsRepository.observeSettings(),
        exchangeRateState
    ) { transactions, categories, selectedId, settings, exchangeState ->
        createUiState(
            transactions = transactions,
            categories = categories,
            selectedId = selectedId,
            displayCurrency = settings.displayCurrency,
            exchangeState = exchangeState
        )
    }
        .catch { exception ->
            LOGGER.severe("Unable to observe dashboard data" + ": " + exception.message)
            emit(
                DashboardUiState(
                    isLoading = false,
                    errorMessage = "Unable to load dashboard data. Please try again."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )

    fun onCategorySelected(categoryId: Long?) {
        selectedCategoryId.update { categoryId }
    }

    private companion object {
        val LOGGER: java.util.logging.Logger = java.util.logging.Logger.getLogger("PennyPilotDashboard")
    }

    private fun createUiState(
        transactions: List<Transaction>,
        categories: List<Category>,
        selectedId: Long?,
        displayCurrency: String,
        exchangeState: ExchangeRateUiState
    ): DashboardUiState {
        val visibleTransactions = if (selectedId == null) {
            transactions
        } else {
            transactions.filter { it.categoryId == selectedId }
        }
        val appliedRate = if (displayCurrency == "MYR") 1.0 else exchangeState.rate?.rate
        val effectiveCurrency = if (appliedRate == null) "MYR" else displayCurrency

        return DashboardUiState(
            isLoading = false,
            transactions = transactions,
            visibleTransactions = visibleTransactions,
            categories = categories,
            selectedCategoryId = selectedId,
            categoryNames = categories.associate { it.id to it.name },
            financialSummary = calculateFinancialSummary(transactions),
            displayCurrency = effectiveCurrency,
            exchangeRate = appliedRate,
            exchangeRateDate = exchangeState.rate?.date,
            isExchangeRateLoading = displayCurrency != "MYR" && exchangeState.isLoading,
            exchangeRateErrorMessage = exchangeState.errorMessage
        )
    }
}
