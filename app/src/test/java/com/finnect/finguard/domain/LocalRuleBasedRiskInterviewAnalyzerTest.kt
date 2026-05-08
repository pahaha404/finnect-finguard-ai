package com.finnect.finguard.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalRuleBasedRiskInterviewAnalyzerTest {
    private val analyzer = LocalRuleBasedRiskInterviewAnalyzer()

    @Test
    fun revolvingMisunderstandingGradesAtLeastRisk() {
        val result = analyze(
            scenarioId = "revolving",
            answers = listOf(
                "네, 리볼빙을 쓰면 이번 달 카드값이 없어지는 것으로 알고 있습니다.",
                "비용은 잘 모르겠고 무료에 가까운 기능 아닌가요.",
                "다음 달에 생각하면 될 것 같습니다.",
            ),
        )

        assertTrue(result.grade.severity >= RiskGrade.RISK.severity)
    }

    @Test
    fun investmentLossThatHarmsLivingCostsGradesHighRisk() {
        val result = analyze(
            scenarioId = "crypto",
            answers = listOf(
                "30% 손실이 나면 생활비와 대출 상환에 문제가 있다.",
                "그래도 가격은 다시 오를 것 같아서 사고 싶다.",
                "손실 한도는 아직 정하지 않았다.",
            ),
        )

        assertEquals(RiskGrade.HIGH_RISK, result.grade)
    }

    @Test
    fun correctRevolvingExplanationGradesSafeOrCaution() {
        val result = analyze(
            scenarioId = "revolving",
            answers = listOf(
                "아닙니다. 카드값이 사라지는 것이 아니라 일부 금액이 다음 달로 이월됩니다.",
                "이월된 금액에는 수수료나 이자성 비용이 붙을 수 있습니다.",
                "장기 이용하면 상환 부담이 커지고 연체나 신용 관리 문제가 생길 수 있습니다.",
            ),
        )

        assertTrue(result.grade == RiskGrade.SAFE || result.grade == RiskGrade.CAUTION)
    }

    @Test
    fun transactionContextCanRaiseOtherwiseCorrectInvestmentToHighRisk() {
        val result = analyze(
            scenarioId = "crypto",
            context = RiskTransactionContext(
                amountWon = 10_000_000,
                affordableLossOrPaymentWon = 1_000_000,
                usesEssentialMoney = false,
                isUrgentToday = false,
            ),
            answers = listOf(
                "원금 손실 가능성이 있고 30% 이상 잃을 수도 있습니다.",
                "가격 변동성이 커서 급락할 수 있습니다.",
                "여유 자금 안에서만 투자하고 손실 한도를 정해야 합니다.",
            ),
        )

        assertEquals(RiskGrade.HIGH_RISK, result.grade)
        assertTrue(result.contextWarnings.any { it.contains("감당 가능 금액") })
    }

    private fun analyze(
        scenarioId: String,
        context: RiskTransactionContext = RiskTransactionContext(
            amountWon = 500_000,
            affordableLossOrPaymentWon = 500_000,
        ),
        answers: List<String>,
    ): RiskInterviewResult {
        val scenario = ScenarioRepository.findById(scenarioId)
            ?: error("Missing scenario $scenarioId")
        return analyzer.analyze(
            RiskInterviewRequest(
                scenario = scenario,
                transactionContext = context,
                answers = scenario.questions.zip(answers).map { (question, answer) ->
                    InterviewAnswer(questionId = question.id, answer = answer)
                },
            ),
        )
    }
}
