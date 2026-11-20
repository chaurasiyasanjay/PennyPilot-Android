# PennyPilot

A native Android personal finance tracker built with Kotlin and Jetpack Compose.

PennyPilot lets users record income and expenses, review financial summaries, filter transactions by category, and choose their preferred theme and display currency.

## Features

- Dashboard with balance, income, expenses, and recent transactions.
- Add and edit income and expense transactions.
- Filter transactions by category.
- Persist transactions and categories locally with Room.
- Save theme and display-currency preferences with DataStore.
- Convert displayed MYR amounts using live Frankfurter exchange rates.
- Fall back to MYR values when exchange-rate retrieval fails.

**Currency handling:** Transaction amounts are stored in MYR. Conversion affects display values only; it does not change the stored transaction amounts.

## Tech stack

Kotlin · Jetpack Compose · Material 3 · MVVM · Clean Architecture · Coroutines · Flow · Room · DataStore · Retrofit · Hilt · Navigation Compose

## Architecture

The app uses three layers: Presentation, Domain, and Data. See [Architecture](docs/ARCHITECTURE.md) for the layer responsibilities and data flows.

## Requirements

- Android Studio
- JDK 17
- Android SDK 37
- Android emulator or physical Android device

## Run the app

1. Clone the repository and open it in Android Studio.
2. Allow Gradle sync to complete.
3. Run the `app` configuration on an emulator or connected device.

The Frankfurter API integration does not require an API key.

## Tests

Run JVM unit tests:

```bash
./gradlew test
```

Run instrumented UI tests on a connected device or emulator:

```bash
./gradlew connectedAndroidTest
```

## Build

Generate a debug APK:

```bash
./gradlew assembleDebug
```

For a signed release APK, use **Build → Generate Signed Bundle / APK** in Android Studio, select **APK**, the `app` module, and the `release` build variant.

Keep the release keystore and passwords private. Never commit signing credentials.

## App configuration

| Property | Value |
|---|---|
| Application ID | `com.pennypilot.app` |
| Minimum SDK | 26 |
| Compile SDK | 37 |
| Target SDK | 36 |
| Java compatibility | 17 |
| Version | 1.0 (1) |
