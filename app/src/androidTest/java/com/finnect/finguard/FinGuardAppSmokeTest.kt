package com.finnect.finguard

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class FinGuardAppSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userCanCompleteScenarioToResultReport() {
        composeRule.onNodeWithTag("scenario-revolving").performClick()
        composeRule.onNodeWithText("이해도 면접 시작").assertIsDisplayed()
        composeRule.onNodeWithTag("transaction-amount")
            .performScrollTo()
            .performTextInput("500000")
        composeRule.onNodeWithTag("affordable-amount")
            .performScrollTo()
            .performTextInput("200000")
        composeRule.onNodeWithTag("start-interview").performClick()

        composeRule.onNodeWithTag("answer-revolving-understanding")
            .performScrollTo()
            .performTextInput("아닙니다. 일부 금액이 다음 달로 이월됩니다.")
        composeRule.onNodeWithTag("answer-revolving-cost")
            .performScrollTo()
            .performTextInput("이월 금액에는 수수료와 이자성 비용이 붙을 수 있습니다.")
        composeRule.onNodeWithTag("answer-revolving-repayment")
            .performScrollTo()
            .performTextInput("다음 달에도 부족하면 상환 부담과 신용 문제가 생길 수 있습니다.")

        composeRule.onNodeWithTag("submit-interview")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithTag("result-report").assertIsDisplayed()
        composeRule.onNodeWithText("위험등급 안전").assertIsDisplayed()
    }
}
