package com.finnect.finguard.domain

enum class ScenarioType {
    REVOLVING,
    CARD_LOAN,
    CRYPTO,
    SURGING_STOCK,
}

enum class RiskGrade(
    val label: String,
    val severity: Int,
) {
    SAFE("안전", 0),
    CAUTION("주의", 1),
    RISK("위험", 2),
    HIGH_RISK("고위험", 3),
}

data class InterviewQuestion(
    val id: String,
    val prompt: String,
    val helperText: String,
)

data class RiskScenario(
    val id: String,
    val type: ScenarioType,
    val title: String,
    val subtitle: String,
    val explanation: String,
    val keyRisks: List<String>,
    val questions: List<InterviewQuestion>,
)

data class InterviewAnswer(
    val questionId: String,
    val answer: String,
)

data class RiskInterviewRequest(
    val scenario: RiskScenario,
    val answers: List<InterviewAnswer>,
)

data class RiskInterviewResult(
    val grade: RiskGrade,
    val summary: String,
    val misunderstoodConcepts: List<String>,
    val riskFactors: List<String>,
    val recommendedActions: List<String>,
)

interface RiskInterviewAnalyzer {
    fun analyze(request: RiskInterviewRequest): RiskInterviewResult
}
