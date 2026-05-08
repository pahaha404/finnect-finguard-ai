package com.finnect.finguard.domain

object ScenarioRepository {
    val scenarios: List<RiskScenario> = listOf(
        RiskScenario(
            id = "revolving",
            type = ScenarioType.REVOLVING,
            title = "리볼빙 사전점검",
            subtitle = "카드값 일부를 다음 달로 넘기기 전",
            explanation = "리볼빙은 이번 달 카드값을 없애는 기능이 아닙니다. 일부 금액을 다음 달로 이월하고, 남은 금액에는 수수료가 붙을 수 있습니다. 오래 쓰면 상환 부담이 커지고 신용 관리에도 영향을 줄 수 있습니다.",
            keyRisks = listOf("결제금액 이월", "수수료 부담", "장기 이용 시 부채 증가", "신용 관리 부담"),
            questions = listOf(
                InterviewQuestion(
                    id = "revolving-understanding",
                    prompt = "리볼빙을 사용하면 이번 달 카드값이 사라지는 건가요?",
                    helperText = "사라지는지, 다음 달로 넘어가는지 본인 말로 설명해 주세요.",
                ),
                InterviewQuestion(
                    id = "revolving-cost",
                    prompt = "다음 달로 넘어간 금액에는 어떤 비용이나 위험이 붙을 수 있나요?",
                    helperText = "수수료, 이월, 장기 이용 부담을 떠올려 보세요.",
                ),
                InterviewQuestion(
                    id = "revolving-repayment",
                    prompt = "다음 달에도 결제 여력이 부족하면 어떤 문제가 생길 수 있나요?",
                    helperText = "상환 부담과 신용 관리 측면에서 답해 주세요.",
                ),
            ),
        ),
        RiskScenario(
            id = "card-loan",
            type = ScenarioType.CARD_LOAN,
            title = "카드론 사전점검",
            subtitle = "빠른 현금 대출을 받기 전",
            explanation = "카드론은 당장 현금을 마련할 수 있지만 빌린 돈보다 더 큰 총상환액을 갚아야 할 수 있습니다. 이자, 월 상환액, 연체 불이익, 신용점수 영향을 함께 이해해야 합니다.",
            keyRisks = listOf("이자와 총상환액", "월 상환 부담", "연체 불이익", "신용점수 영향"),
            questions = listOf(
                InterviewQuestion(
                    id = "card-loan-total",
                    prompt = "카드론을 받으면 실제로 갚아야 할 돈은 빌린 금액과 어떻게 달라질까요?",
                    helperText = "이자와 총상환액 관점으로 설명해 주세요.",
                ),
                InterviewQuestion(
                    id = "card-loan-credit",
                    prompt = "연체되면 신용점수나 다음 금융거래에 어떤 영향이 있을까요?",
                    helperText = "대출, 카드, 금리 조건에 미칠 수 있는 영향을 적어 주세요.",
                ),
                InterviewQuestion(
                    id = "card-loan-burden",
                    prompt = "월 상환액이 생활비를 압박하면 어떤 선택을 해야 할까요?",
                    helperText = "진행, 보류, 상담, 금액 조정 중 무엇이 안전한지 답해 주세요.",
                ),
            ),
        ),
        RiskScenario(
            id = "crypto",
            type = ScenarioType.CRYPTO,
            title = "가상자산 매수 점검",
            subtitle = "급등한 코인을 사기 전",
            explanation = "가상자산은 가격 변동이 크고 원금 손실 가능성이 있습니다. 단기 수익 기대만으로 생활비나 대출 상환에 필요한 돈을 투자하면 손실이 곧 생활 위험으로 이어질 수 있습니다.",
            keyRisks = listOf("높은 변동성", "원금 손실 가능성", "유동성 위험", "손실 감내 한도"),
            questions = listOf(
                InterviewQuestion(
                    id = "crypto-loss",
                    prompt = "투자금이 30% 손실 나도 생활비나 대출 상환에 문제가 없나요?",
                    helperText = "문제가 있는지 없는지와 이유를 함께 적어 주세요.",
                ),
                InterviewQuestion(
                    id = "crypto-volatility",
                    prompt = "가격이 급하게 오르내릴 때 어떤 위험이 있나요?",
                    helperText = "변동성, 원금 손실, 매도 어려움을 고려해 주세요.",
                ),
                InterviewQuestion(
                    id = "crypto-limit",
                    prompt = "원금을 잃을 가능성을 어떻게 제한할 수 있나요?",
                    helperText = "투자 금액, 기간, 손실 한도 기준을 적어 주세요.",
                ),
            ),
        ),
        RiskScenario(
            id = "surging-stock",
            type = ScenarioType.SURGING_STOCK,
            title = "급등주 매수 점검",
            subtitle = "화제가 된 종목을 따라 사기 전",
            explanation = "급등주는 이미 기대가 가격에 반영되어 있을 수 있고, 급락하면 원금 손실이 발생할 수 있습니다. 뉴스나 커뮤니티 분위기보다 손실 가능 금액과 투자 이유를 먼저 확인해야 합니다.",
            keyRisks = listOf("추격 매수 위험", "급락 가능성", "원금 손실", "손실 감내 한도"),
            questions = listOf(
                InterviewQuestion(
                    id = "stock-reason",
                    prompt = "이 종목을 사려는 이유가 단기 분위기 말고 무엇인가요?",
                    helperText = "실적, 가격, 위험, 투자 기간 중 확인한 내용을 적어 주세요.",
                ),
                InterviewQuestion(
                    id = "stock-loss",
                    prompt = "투자금이 30% 손실 나도 생활비나 대출 상환에 문제가 없나요?",
                    helperText = "손실을 감당할 수 있는 돈인지 답해 주세요.",
                ),
                InterviewQuestion(
                    id = "stock-limit",
                    prompt = "급락하면 언제 멈추거나 줄일 계획인가요?",
                    helperText = "손실 한도와 보류 기준을 적어 주세요.",
                ),
            ),
        ),
    )

    fun findById(id: String): RiskScenario? = scenarios.firstOrNull { it.id == id }
}
