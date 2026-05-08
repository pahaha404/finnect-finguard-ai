package com.finnect.finguard.domain

import java.util.Locale

class LocalRuleBasedRiskInterviewAnalyzer : RiskInterviewAnalyzer {
    override fun analyze(request: RiskInterviewRequest): RiskInterviewResult {
        val rule = rules.getValue(request.scenario.type)
        val combinedText = request.answers.joinToString(separator = " ") { it.answer }.normalized()
        val matchedConcepts = rule.requiredConcepts.filter { concept ->
            concept.keywords.any { combinedText.contains(it) }
        }
        val missingConcepts = rule.requiredConcepts
            .filterNot { matchedConcepts.contains(it) }
            .map { it.label }
        val misunderstandings = rule.misunderstandings
            .filter { signal -> signal.keywords.any { combinedText.contains(it) } }
            .map { it.label }
        val hasBurden = detectsRepaymentOrLossBurden(combinedText)
        val hasInvestmentBurden = request.scenario.type in investmentTypes && hasBurden
        val transactionEstimate = RiskTransactionEstimator.estimate(
            scenarioType = request.scenario.type,
            context = request.transactionContext,
        )
        val contextWarnings = transactionEstimate.warnings
        val contextGrade = transactionEstimate.precheckGrade

        val initialGrade = when {
            hasInvestmentBurden -> RiskGrade.HIGH_RISK
            misunderstandings.isNotEmpty() && hasBurden -> RiskGrade.HIGH_RISK
            misunderstandings.isNotEmpty() -> RiskGrade.RISK
            matchedConcepts.size == rule.requiredConcepts.size -> RiskGrade.SAFE
            matchedConcepts.size >= rule.requiredConcepts.size - 1 -> RiskGrade.CAUTION
            else -> RiskGrade.RISK
        }
        val grade = if (hasBurden && initialGrade.severity >= RiskGrade.RISK.severity) {
            RiskGrade.HIGH_RISK
        } else {
            initialGrade
        }.max(contextGrade)

        val riskFactors = buildList {
            addAll(misunderstandings)
            addAll(missingConcepts.map { "확인 필요: $it" })
            if (hasBurden) add("상환 또는 손실이 생활비와 대출 상환에 부담을 줄 수 있음")
            if (isEmpty()) add("핵심 위험을 대체로 이해하고 있음")
        }.distinct()

        return RiskInterviewResult(
            grade = grade,
            summary = summaryFor(grade, request.scenario),
            understoodConcepts = matchedConcepts.map { it.label },
            misunderstoodConcepts = if (misunderstandings.isEmpty()) missingConcepts else misunderstandings,
            contextWarnings = contextWarnings,
            riskFactors = riskFactors,
            followUpQuestions = followUpQuestionsFor(missingConcepts, request.scenario),
            recommendedActions = actionsFor(grade, request.scenario),
            saferAlternatives = saferAlternativesFor(grade, request.scenario),
            coolingOffNotice = coolingOffNoticeFor(grade),
        )
    }

    private fun detectsRepaymentOrLossBurden(text: String): Boolean {
        val burdenPhrases = listOf(
            "문제가 있다",
            "문제 있다",
            "문제될",
            "힘들",
            "어렵",
            "못 갚",
            "못갚",
            "못 낼",
            "못낼",
            "감당 안",
            "감당이 안",
            "생활비가 부족",
            "상환 불가",
            "연체될",
            "연체할",
        )
        val hasDirectBurden = burdenPhrases.any { text.contains(it) }
        val livingCostBurden = text.contains("생활비") &&
            listOf("부족", "문제", "힘들", "어렵", "못").any { text.contains(it) }
        val debtBurden = text.contains("대출") &&
            text.contains("상환") &&
            listOf("문제", "힘들", "어렵", "못", "불가").any { text.contains(it) }

        return hasDirectBurden || livingCostBurden || debtBurden
    }

    private fun summaryFor(
        grade: RiskGrade,
        scenario: RiskScenario,
    ): String = when (grade) {
        RiskGrade.SAFE -> "${scenario.title}의 핵심 위험을 이해하고 있습니다. 결정 전 금액과 기간만 다시 확인하세요."
        RiskGrade.CAUTION -> "일부 개념은 이해했지만 빠진 위험이 있습니다. 추가 설명을 읽고 답을 다시 점검하는 편이 안전합니다."
        RiskGrade.RISK -> "핵심 위험을 오해했거나 중요한 비용과 손실 가능성이 빠졌습니다. 즉시 진행하기보다 잠시 멈춰 재검토하세요."
        RiskGrade.HIGH_RISK -> "이해 부족과 상환 또는 손실 부담이 함께 보입니다. 진행을 늦추고 상담이나 더 낮은 위험의 대안을 먼저 확인하세요."
    }

    private fun actionsFor(
        grade: RiskGrade,
        scenario: RiskScenario,
    ): List<String> = when (grade) {
        RiskGrade.SAFE -> listOf(
            "금액, 기간, 수수료 또는 손실 한도를 한 번 더 확인하세요.",
            "결정 기록을 남기고 충동적으로 금액을 늘리지 마세요.",
        )
        RiskGrade.CAUTION -> listOf(
            "빠진 개념을 다시 읽고 같은 질문에 다시 답해 보세요.",
            "진행한다면 금액을 줄이고 상환 또는 손실 한도를 먼저 정하세요.",
        )
        RiskGrade.RISK -> listOf(
            "오늘 바로 진행하지 말고 냉각 시간을 두세요.",
            "${scenario.title}의 비용, 손실, 신용 영향을 다시 확인하세요.",
            "더 낮은 금액이나 대체 선택지를 비교하세요.",
        )
        RiskGrade.HIGH_RISK -> listOf(
            "진행을 지연하고 금융 상담 또는 신뢰할 수 있는 보호자와 상의하세요.",
            "생활비, 월세, 대출 상환에 필요한 돈은 사용하지 마세요.",
            "상환 계획이나 손실 한도가 명확해질 때까지 대안을 우선 검토하세요.",
        )
    }

    private fun followUpQuestionsFor(
        missingConcepts: List<String>,
        scenario: RiskScenario,
    ): List<String> = if (missingConcepts.isEmpty()) {
        listOf("결정 전에 ${scenario.title}의 금액, 기간, 중단 기준을 다시 말로 설명해 보세요.")
    } else {
        missingConcepts.map { concept -> "$concept: 이 내용을 본인 말로 다시 설명해 보세요." }
    }

    private fun saferAlternativesFor(
        grade: RiskGrade,
        scenario: RiskScenario,
    ): List<String> = when (scenario.type) {
        ScenarioType.REVOLVING -> listOf(
            "카드사 앱에서 결제일 변경, 분할 납부, 즉시결제 가능 금액을 비교하세요.",
            "필수 지출을 제외한 예산을 다시 잡고 다음 달 이월액을 줄이세요.",
            "반복 사용 중이면 카드사 또는 서민금융 상담을 먼저 확인하세요.",
        )
        ScenarioType.CARD_LOAN -> listOf(
            "월 상환액이 생활비를 침범하지 않는 더 낮은 금액으로 줄이세요.",
            "은행권 대출, 정책금융, 채무상담처럼 금리와 상환 조건이 낮은 대안을 비교하세요.",
            "급전 목적이면 오늘 신청 전 최소 하루 냉각 시간을 두세요.",
        )
        ScenarioType.CRYPTO -> listOf(
            "생활비와 대출 상환금을 제외한 여유 자금만 사용하세요.",
            "매수 금액을 줄이고 손실 한도를 먼저 정하세요.",
            "즉시 매수 대신 관망, 모의투자, 소액 분할 접근을 검토하세요.",
        )
        ScenarioType.SURGING_STOCK -> listOf(
            "뉴스나 커뮤니티 분위기보다 매수 이유와 손절 기준을 먼저 적어 보세요.",
            "급등 직후 전액 매수보다 금액 축소, 분할, 관망을 검토하세요.",
            "생활비에 영향을 주는 돈이면 매수를 보류하세요.",
        )
    }.let { alternatives ->
        if (grade == RiskGrade.SAFE) alternatives.take(2) else alternatives
    }

    private fun coolingOffNoticeFor(grade: RiskGrade): String = when (grade) {
        RiskGrade.SAFE -> "냉각 시간은 필수는 아니지만, 금액을 늘리기 전에는 같은 점검을 다시 진행하세요."
        RiskGrade.CAUTION -> "최소 30분 뒤 같은 질문에 다시 답하고 빠진 개념이 없는지 확인하세요."
        RiskGrade.RISK -> "오늘 바로 진행하지 말고 최소 24시간 뒤 금액과 대안을 다시 비교하세요."
        RiskGrade.HIGH_RISK -> "즉시 진행을 늦추고 상담 또는 신뢰할 수 있는 사람과 함께 조건을 재검토하세요."
    }

    private fun String.normalized(): String = lowercase(Locale.KOREAN)
        .replace("\\s+".toRegex(), " ")
        .trim()

    private fun RiskGrade.max(other: RiskGrade): RiskGrade =
        if (severity >= other.severity) this else other

    private data class RequiredConcept(
        val label: String,
        val keywords: List<String>,
    )

    private data class MisunderstandingSignal(
        val label: String,
        val keywords: List<String>,
    )

    private data class RuleSet(
        val requiredConcepts: List<RequiredConcept>,
        val misunderstandings: List<MisunderstandingSignal>,
    )

    private companion object {
        private val investmentTypes = setOf(ScenarioType.CRYPTO, ScenarioType.SURGING_STOCK)

        private val rules = mapOf(
            ScenarioType.REVOLVING to RuleSet(
                requiredConcepts = listOf(
                    RequiredConcept("카드값은 사라지지 않고 다음 달로 이월됨", listOf("이월", "다음 달", "넘어")),
                    RequiredConcept("이월 금액에는 수수료나 이자성 비용이 붙을 수 있음", listOf("수수료", "이자", "비용")),
                    RequiredConcept("장기 이용 시 상환 부담과 신용 관리 위험이 커짐", listOf("상환", "부담", "신용", "연체")),
                ),
                misunderstandings = listOf(
                    MisunderstandingSignal("리볼빙을 카드값이 사라지는 기능으로 오해함", listOf("사라집", "사라진", "없어", "안 내도", "안내도", "무료")),
                ),
            ),
            ScenarioType.CARD_LOAN to RuleSet(
                requiredConcepts = listOf(
                    RequiredConcept("빌린 금액보다 총상환액이 커질 수 있음", listOf("총상환", "상환액", "갚", "원리금")),
                    RequiredConcept("이자와 금리 부담이 있음", listOf("이자", "금리", "수수료")),
                    RequiredConcept("연체와 신용점수 영향이 있음", listOf("연체", "신용", "점수")),
                ),
                misunderstandings = listOf(
                    MisunderstandingSignal("카드론을 수수료 없는 현금처럼 오해함", listOf("공짜", "무료", "그냥 현금", "이자 없어", "이자없")),
                    MisunderstandingSignal("상환 부담을 고려하지 않음", listOf("나중에 생각", "언젠가 갚", "일단 빌리")),
                ),
            ),
            ScenarioType.CRYPTO to RuleSet(
                requiredConcepts = listOf(
                    RequiredConcept("원금 손실 가능성이 있음", listOf("원금", "손실", "잃")),
                    RequiredConcept("가격 변동성이 큼", listOf("변동", "급락", "오르내")),
                    RequiredConcept("손실 감내 한도 안에서만 투자해야 함", listOf("한도", "감당", "생활비", "여유 자금", "여윳돈")),
                ),
                misunderstandings = listOf(
                    MisunderstandingSignal("가상자산을 확정 수익으로 오해함", listOf("무조건", "확실", "보장", "반드시 오")),
                    MisunderstandingSignal("생활자금으로 투자하려 함", listOf("생활비로", "대출해서", "빚내서")),
                ),
            ),
            ScenarioType.SURGING_STOCK to RuleSet(
                requiredConcepts = listOf(
                    RequiredConcept("급등 후 급락과 원금 손실 가능성이 있음", listOf("급락", "손실", "원금", "잃")),
                    RequiredConcept("단기 분위기나 추격 매수 위험이 있음", listOf("추격", "분위기", "뉴스", "고점")),
                    RequiredConcept("손실 한도와 중단 기준이 필요함", listOf("한도", "손절", "줄이", "멈추", "감당")),
                ),
                misunderstandings = listOf(
                    MisunderstandingSignal("급등주를 확정 수익으로 오해함", listOf("무조건", "확실", "보장", "계속 오")),
                    MisunderstandingSignal("분위기만 보고 매수하려 함", listOf("남들이", "커뮤니티", "유행", "따라")),
                ),
            ),
        )
    }
}
