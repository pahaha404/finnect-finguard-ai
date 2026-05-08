package com.finnect.finguard.ui

import androidx.lifecycle.ViewModel
import com.finnect.finguard.domain.InterviewAnswer
import com.finnect.finguard.domain.LocalRuleBasedRiskInterviewAnalyzer
import com.finnect.finguard.domain.RiskInterviewAnalyzer
import com.finnect.finguard.domain.RiskInterviewRequest
import com.finnect.finguard.domain.RiskInterviewResult
import com.finnect.finguard.domain.RiskScenario
import com.finnect.finguard.domain.RiskTransactionEstimate
import com.finnect.finguard.domain.RiskTransactionContext
import com.finnect.finguard.domain.RiskTransactionEstimator
import com.finnect.finguard.domain.ScenarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class FinGuardStep {
    SCENARIO_SELECTION,
    EXPLANATION,
    INTERVIEW,
    RESULT,
}

data class FinGuardUiState(
    val scenarios: List<RiskScenario> = ScenarioRepository.scenarios,
    val currentStep: FinGuardStep = FinGuardStep.SCENARIO_SELECTION,
    val selectedScenario: RiskScenario? = null,
    val amountText: String = "",
    val affordableText: String = "",
    val usesEssentialMoney: Boolean = false,
    val isUrgentToday: Boolean = false,
    val answers: Map<String, String> = emptyMap(),
    val result: RiskInterviewResult? = null,
) {
    val canStartInterview: Boolean
        get() = amountText.onlyDigits().isNotBlank() && affordableText.onlyDigits().isNotBlank()

    val transactionEstimate: RiskTransactionEstimate?
        get() = selectedScenario
            ?.takeIf { canStartInterview }
            ?.let { scenario ->
                RiskTransactionEstimator.estimate(
                    scenarioType = scenario.type,
                    context = toTransactionContext(),
                )
            }

    val canSubmit: Boolean
        get() = selectedScenario?.questions?.all { question ->
            answers[question.id].orEmpty().isNotBlank()
        } == true
}

class FinGuardViewModel(
    private val analyzer: RiskInterviewAnalyzer = LocalRuleBasedRiskInterviewAnalyzer(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(FinGuardUiState())
    val uiState: StateFlow<FinGuardUiState> = _uiState

    fun selectScenario(scenario: RiskScenario) {
        _uiState.update {
            it.copy(
                currentStep = FinGuardStep.EXPLANATION,
                selectedScenario = scenario,
                amountText = "",
                affordableText = "",
                usesEssentialMoney = false,
                isUrgentToday = false,
                answers = emptyMap(),
                result = null,
            )
        }
    }

    fun startInterview() {
        if (!_uiState.value.canStartInterview) return
        _uiState.update { it.copy(currentStep = FinGuardStep.INTERVIEW) }
    }

    fun updateAmountText(value: String) {
        _uiState.update { it.copy(amountText = value.filterAmountInput()) }
    }

    fun updateAffordableText(value: String) {
        _uiState.update { it.copy(affordableText = value.filterAmountInput()) }
    }

    fun setUsesEssentialMoney(value: Boolean) {
        _uiState.update { it.copy(usesEssentialMoney = value) }
    }

    fun setUrgentToday(value: Boolean) {
        _uiState.update { it.copy(isUrgentToday = value) }
    }

    fun updateAnswer(
        questionId: String,
        answer: String,
    ) {
        _uiState.update {
            it.copy(answers = it.answers + (questionId to answer))
        }
    }

    fun submitInterview() {
        val state = _uiState.value
        val scenario = state.selectedScenario ?: return
        if (!state.canSubmit) return

        val request = RiskInterviewRequest(
            scenario = scenario,
            transactionContext = state.toTransactionContext(),
            answers = scenario.questions.map { question ->
                InterviewAnswer(
                    questionId = question.id,
                    answer = state.answers[question.id].orEmpty(),
                )
            },
        )

        _uiState.update {
            it.copy(
                currentStep = FinGuardStep.RESULT,
                result = analyzer.analyze(request),
            )
        }
    }

    fun backToScenarios() {
        _uiState.value = FinGuardUiState()
    }

    fun backToExplanation() {
        _uiState.update { it.copy(currentStep = FinGuardStep.EXPLANATION, result = null) }
    }

    fun reviseAnswers() {
        _uiState.update { it.copy(currentStep = FinGuardStep.INTERVIEW, result = null) }
    }

    fun editTransactionContext() {
        _uiState.update { it.copy(currentStep = FinGuardStep.EXPLANATION, result = null) }
    }
}

private fun FinGuardUiState.toTransactionContext(): RiskTransactionContext =
    RiskTransactionContext(
        amountWon = amountText.onlyDigits().toLongOrNull(),
        affordableLossOrPaymentWon = affordableText.onlyDigits().toLongOrNull(),
        usesEssentialMoney = usesEssentialMoney,
        isUrgentToday = isUrgentToday,
    )

private fun String.filterAmountInput(): String =
    filter { it.isDigit() }.take(12)

private fun String.onlyDigits(): String =
    filter { it.isDigit() }
