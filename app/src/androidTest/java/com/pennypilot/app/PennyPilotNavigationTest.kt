package com.pennypilot.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class PennyPilotNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun dashboard_opensAddTransactionForm() {
        composeRule.onNodeWithText("Your overview").assertIsDisplayed()

        composeRule.onNodeWithText("+ Add").performClick()

        composeRule.onNodeWithText("Add transaction").assertIsDisplayed()
        composeRule.onNodeWithText("Amount (MYR)").assertIsDisplayed()
        composeRule.onNodeWithText("Save transaction").assertIsDisplayed()
    }
}
