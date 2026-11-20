# PennyPilot Architecture

## Architectural style

PennyPilot uses **Clean Architecture** with **MVVM** in the Presentation layer. The application is organised into three layers:

- **Presentation** — Jetpack Compose screens, ViewModels, UI state, and navigation.
- **Domain** — business models, repository contracts, and use cases.
- **Data** — repository implementations and integrations with local and remote data sources.

The dependency direction is:

```text
Presentation → Domain ← Data
```

Presentation depends on domain contracts. Data implements the repository interfaces defined by Domain. Domain does not depend on Presentation or Data implementations. Hilt wires the application dependencies.

## Layer responsibilities

### Presentation

- Compose screens render application state and collect user interactions.
- ViewModels coordinate screen behaviour and expose UI state through StateFlow.
- Kotlin Flow provides reactive observation of repository and preference data.
- Navigation Compose connects Dashboard, Add/Edit Transaction, and Settings.

### Domain

- Models represent transactions, categories, application settings, and exchange rates.
- Repository interfaces define contracts for transactions, categories, settings, and exchange rates.
- Use cases encapsulate financial summary calculation and exchange-rate retrieval.

### Data

- Room entities, DAOs, and the database persist transactions and categories.
- DataStore persists theme and display-currency preferences.
- Retrofit communicates with the Frankfurter exchange-rate API.
- DTO mappers convert remote responses into domain models.
- Repository implementations connect the data sources to domain contracts.

## Main data flows

### Transactions

1. A user action is received by a Compose screen.
2. The screen delegates the action to its ViewModel.
3. The ViewModel invokes domain logic through a use case or repository contract.
4. The data repository performs the operation using Room.
5. Room emits updated data through Flow.
6. The ViewModel exposes the latest state and Compose renders it.

### Settings

1. The Settings ViewModel observes application preferences through the settings repository.
2. The DataStore implementation reads and persists the theme and display currency.
3. The selected theme is applied at the app root.
4. The selected currency is observed by the dashboard.

### Exchange rates

1. The Dashboard observes the selected display currency.
2. When the selected currency differs from MYR, the dashboard requests the MYR exchange rate through the domain exchange-rate contract.
3. The data repository calls the Frankfurter v2 endpoint using Retrofit.
4. The response is mapped to the ExchangeRate domain model.
5. The dashboard applies the rate to summary and transaction amounts for display.
6. Original transaction values remain stored in MYR minor units. If the request fails, the dashboard falls back to MYR values and presents a warning.

## Persistence

Room stores:

- Transactions
- Categories

The database creation callback seeds the built-in income and expense categories when the database is first created.

DataStore stores:

- Theme preference (System, Light, or Dark)
- Display currency

## Dependency injection

Hilt provides and binds the application dependencies, including:

- Room database and DAOs
- Retrofit client and exchange-rate API
- Repository implementations
- DataStore-backed settings repository
- ViewModels and their dependencies

## Package organisation

The source code is organised by architectural responsibility under `com.pennypilot.app`:

- `data` — local persistence, remote API, DTO mapping, and repository implementations.
- `domain` — models, repository interfaces, and use cases.
- `presentation` — dashboard, transaction forms, settings, UI state, and navigation.
- `di` — Hilt modules for dependency provision and binding.
