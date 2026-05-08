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
    val aiAction: String,
    val userOutcome: String,
) {
    SAFE("안전", 0, "요약 리포트 제공", "거래 진행 가능"),
    CAUTION("주의", 1, "추가 설명 후 재질문", "재확인 후 진행"),
    RISK("위험", 2, "거래 재고 권고", "쿨링오프 안내"),
    HIGH_RISK("고위험", 3, "상담 연결 및 대안 제시", "진행 지연 권고"),
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

data class RiskTransactionContext(
    val amountWon: Long? = null,
    val affordableLossOrPaymentWon: Long? = null,
    val usesEssentialMoney: Boolean = false,
    val isUrgentToday: Boolean = false,
)

data class RiskTransactionEstimate(
    val pressureLabel: String,
    val projectedPressureWon: Long?,
    val precheckGrade: RiskGrade,
    val headline: String,
    val warnings: List<String>,
)

object RiskTransactionEstimator {
    private val investmentTypes = setOf(ScenarioType.CRYPTO, ScenarioType.SURGING_STOCK)

    fun estimate(
        scenarioType: ScenarioType,
        context: RiskTransactionContext,
    ): RiskTransactionEstimate {
        val amount = context.amountWon
        val affordable = context.affordableLossOrPaymentWon
        val projectedPressure = amount?.let { projectedPressureWon(scenarioType, it) }
        val pressureLabel = if (scenarioType in investmentTypes) "30% 손실 추정액" else "초기 상환 부담 추정액"
        val precheckGrade = gradeFromContext(scenarioType, context, projectedPressure)
        val warnings = contextWarningsFor(scenarioType, context, projectedPressure)

        return RiskTransactionEstimate(
            pressureLabel = pressureLabel,
            projectedPressureWon = projectedPressure,
            precheckGrade = precheckGrade,
            headline = headlineFor(precheckGrade),
            warnings = warnings,
        )
    }

    fun projectedPressureWon(
        scenarioType: ScenarioType,
        amountWon: Long,
    ): Long = when (scenarioType) {
        ScenarioType.REVOLVING -> (amountWon * 0.15).toLong().coerceAtLeast(1L)
        ScenarioType.CARD_LOAN -> (amountWon * 0.20).toLong().coerceAtLeast(1L)
        ScenarioType.CRYPTO,
        ScenarioType.SURGING_STOCK,
        -> (amountWon * 0.30).toLong().coerceAtLeast(1L)
    }

    private fun gradeFromContext(
        scenarioType: ScenarioType,
        context: RiskTransactionContext,
        projectedPressure: Long?,
    ): RiskGrade {
        val affordable = context.affordableLossOrPaymentWon

        return when {
            context.usesEssentialMoney && context.isUrgentToday -> RiskGrade.HIGH_RISK
            context.usesEssentialMoney -> RiskGrade.RISK
            projectedPressure != null && affordable != null && projectedPressure > affordable * 2 -> RiskGrade.HIGH_RISK
            projectedPressure != null && affordable != null && projectedPressure > affordable -> RiskGrade.RISK
            context.isUrgentToday && scenarioType in investmentTypes -> RiskGrade.RISK
            context.isUrgentToday -> RiskGrade.CAUTION
            else -> RiskGrade.SAFE
        }
    }

    private fun contextWarningsFor(
        scenarioType: ScenarioType,
        context: RiskTransactionContext,
        projectedPressure: Long?,
    ): List<String> = buildList {
        val affordable = context.affordableLossOrPaymentWon
        if (context.usesEssentialMoney) {
            add("생활비나 대출 상환에 필요한 돈이 포함되어 있음")
        }
        if (context.isUrgentToday) {
            add("오늘 바로 결정하려는 압박이 있어 냉각 시간이 필요함")
        }
        if (projectedPressure != null && affordable != null) {
            val label = if (scenarioType in investmentTypes) "30% 손실 추정액" else "초기 상환 부담 추정액"
            if (projectedPressure > affordable) {
                add("$label ${projectedPressure.toKoreanWon()}이 감당 가능 금액 ${affordable.toKoreanWon()}을 초과함")
            } else {
                add("$label ${projectedPressure.toKoreanWon()}이 입력한 감당 가능 범위 안에 있음")
            }
        }
    }

    private fun headlineFor(grade: RiskGrade): String = when (grade) {
        RiskGrade.SAFE -> "입력한 조건만 보면 즉시 가중되는 위험은 낮습니다."
        RiskGrade.CAUTION -> "결정 압박이 있어 한 번 더 확인하는 편이 안전합니다."
        RiskGrade.RISK -> "거래 조건만으로도 재검토가 필요한 위험이 있습니다."
        RiskGrade.HIGH_RISK -> "거래 조건상 진행을 늦추는 것이 안전합니다."
    }
}

data class RiskInterviewRequest(
    val scenario: RiskScenario,
    val transactionContext: RiskTransactionContext = RiskTransactionContext(),
    val answers: List<InterviewAnswer>,
)

data class RiskInterviewResult(
    val grade: RiskGrade,
    val summary: String,
    val understoodConcepts: List<String>,
    val misunderstoodConcepts: List<String>,
    val contextWarnings: List<String>,
    val riskFactors: List<String>,
    val followUpQuestions: List<String>,
    val recommendedActions: List<String>,
    val saferAlternatives: List<String>,
    val coolingOffNotice: String,
)

interface RiskInterviewAnalyzer {
    fun analyze(request: RiskInterviewRequest): RiskInterviewResult
}

fun Long.toKoreanWon(): String = "%,d원".format(this)
