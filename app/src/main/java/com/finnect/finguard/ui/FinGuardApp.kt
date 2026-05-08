package com.finnect.finguard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.finnect.finguard.R
import com.finnect.finguard.domain.RiskGrade
import com.finnect.finguard.domain.RiskInterviewResult
import com.finnect.finguard.domain.RiskScenario
import com.finnect.finguard.domain.RiskTransactionEstimate
import com.finnect.finguard.domain.toKoreanWon

@Composable
fun FinGuardApp(viewModel: FinGuardViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FinGuardAppContent(
        state = state,
        onScenarioSelected = viewModel::selectScenario,
        onStartInterview = viewModel::startInterview,
        onAmountChanged = viewModel::updateAmountText,
        onAffordableChanged = viewModel::updateAffordableText,
        onEssentialMoneyChanged = viewModel::setUsesEssentialMoney,
        onUrgentTodayChanged = viewModel::setUrgentToday,
        onAnswerChanged = viewModel::updateAnswer,
        onSubmit = viewModel::submitInterview,
        onBackToScenarios = viewModel::backToScenarios,
        onBackToExplanation = viewModel::backToExplanation,
        onReviseAnswers = viewModel::reviseAnswers,
        onEditTransactionContext = viewModel::editTransactionContext,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinGuardAppContent(
    state: FinGuardUiState,
    onScenarioSelected: (RiskScenario) -> Unit,
    onStartInterview: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onAffordableChanged: (String) -> Unit,
    onEssentialMoneyChanged: (Boolean) -> Unit,
    onUrgentTodayChanged: (Boolean) -> Unit,
    onAnswerChanged: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onBackToScenarios: () -> Unit,
    onBackToExplanation: () -> Unit,
    onReviseAnswers: () -> Unit,
    onEditTransactionContext: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FinGuard AI") },
                navigationIcon = {
                    if (state.currentStep != FinGuardStep.SCENARIO_SELECTION) {
                        IconButton(onClick = onBackToScenarios) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "처음으로")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (state.currentStep) {
                FinGuardStep.SCENARIO_SELECTION -> ScenarioSelectionScreen(
                    scenarios = state.scenarios,
                    onScenarioSelected = onScenarioSelected,
                )
                FinGuardStep.EXPLANATION -> state.selectedScenario?.let {
                    ExplanationScreen(
                        scenario = it,
                        amountText = state.amountText,
                        affordableText = state.affordableText,
                        usesEssentialMoney = state.usesEssentialMoney,
                        isUrgentToday = state.isUrgentToday,
                        transactionEstimate = state.transactionEstimate,
                        canStartInterview = state.canStartInterview,
                        onAmountChanged = onAmountChanged,
                        onAffordableChanged = onAffordableChanged,
                        onEssentialMoneyChanged = onEssentialMoneyChanged,
                        onUrgentTodayChanged = onUrgentTodayChanged,
                        onStartInterview = onStartInterview,
                    )
                }
                FinGuardStep.INTERVIEW -> state.selectedScenario?.let {
                    InterviewScreen(
                        scenario = it,
                        answers = state.answers,
                        canSubmit = state.canSubmit,
                        onAnswerChanged = onAnswerChanged,
                        onSubmit = onSubmit,
                        onBack = onBackToExplanation,
                    )
                }
                FinGuardStep.RESULT -> {
                    val scenario = state.selectedScenario
                    val result = state.result
                    if (scenario != null && result != null) {
                        ResultScreen(
                            scenario = scenario,
                            result = result,
                            onRestart = onBackToScenarios,
                            onReviseAnswers = onReviseAnswers,
                            onEditTransactionContext = onEditTransactionContext,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InterviewerPanel(
    title: String,
    message: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interviewer-panel"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(82.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.interviewer_portrait),
                    contentDescription = "FinGuard AI 면접관",
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InterviewerQuestionBubble(prompt: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Image(
                painter = painterResource(id = R.drawable.interviewer_portrait),
                contentDescription = null,
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxSize(),
            )
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "FinGuard 면접관",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = prompt,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ScenarioSelectionScreen(
    scenarios: List<RiskScenario>,
    onScenarioSelected: (RiskScenario) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            HeaderBlock(
                eyebrow = "위험 금융거래 전 이해도 점검",
                title = "결정하기 전에 위험을 본인 말로 확인하세요",
                body = "리볼빙, 카드론, 고위험 투자처럼 손실과 상환 부담이 큰 선택 직전에 짧은 면접을 진행합니다.",
            )
        }
        items(scenarios, key = { it.id }) { scenario ->
            ScenarioCard(
                scenario = scenario,
                onClick = { onScenarioSelected(scenario) },
            )
        }
    }
}

@Composable
private fun ScenarioCard(
    scenario: RiskScenario,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scenario-${scenario.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scenario.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = scenario.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ExplanationScreen(
    scenario: RiskScenario,
    amountText: String,
    affordableText: String,
    usesEssentialMoney: Boolean,
    isUrgentToday: Boolean,
    transactionEstimate: RiskTransactionEstimate?,
    canStartInterview: Boolean,
    onAmountChanged: (String) -> Unit,
    onAffordableChanged: (String) -> Unit,
    onEssentialMoneyChanged: (Boolean) -> Unit,
    onUrgentTodayChanged: (Boolean) -> Unit,
    onStartInterview: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HeaderBlock(
                eyebrow = scenario.subtitle,
                title = scenario.title,
                body = scenario.explanation,
            )
        }
        item {
            InterviewerPanel(
                title = "FinGuard AI 면접관",
                message = "거래를 막으려는 것이 아니라, 위험을 이해한 상태에서 선택하도록 돕겠습니다. 먼저 조건을 확인한 뒤 짧게 질문드릴게요.",
            )
        }
        item {
            RiskChips(risks = scenario.keyRisks)
        }
        item {
            TransactionContextForm(
                amountText = amountText,
                affordableText = affordableText,
                usesEssentialMoney = usesEssentialMoney,
                isUrgentToday = isUrgentToday,
                onAmountChanged = onAmountChanged,
                onAffordableChanged = onAffordableChanged,
                onEssentialMoneyChanged = onEssentialMoneyChanged,
                onUrgentTodayChanged = onUrgentTodayChanged,
            )
        }
        if (transactionEstimate != null) {
            item {
                TransactionPrecheckPanel(estimate = transactionEstimate)
            }
        }
        item {
            Button(
                onClick = onStartInterview,
                enabled = canStartInterview,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start-interview"),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("이해도 면접 시작")
            }
            if (!canStartInterview) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "거래금액과 감당 가능한 금액을 입력하면 면접을 시작할 수 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TransactionContextForm(
    amountText: String,
    affordableText: String,
    usesEssentialMoney: Boolean,
    isUrgentToday: Boolean,
    onAmountChanged: (String) -> Unit,
    onAffordableChanged: (String) -> Unit,
    onEssentialMoneyChanged: (Boolean) -> Unit,
    onUrgentTodayChanged: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "거래 조건",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = amountText,
                onValueChange = onAmountChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction-amount"),
                label = { Text("거래금액") },
                suffix = { Text("원") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )
            OutlinedTextField(
                value = affordableText,
                onValueChange = onAffordableChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("affordable-amount"),
                label = { Text("감당 가능한 상환액 또는 손실액") },
                suffix = { Text("원") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
            )
            BinaryCheckRow(
                checked = usesEssentialMoney,
                onCheckedChange = onEssentialMoneyChanged,
                label = "생활비나 대출 상환에 필요한 돈이 포함되어 있음",
                tag = "uses-essential-money",
            )
            BinaryCheckRow(
                checked = isUrgentToday,
                onCheckedChange = onUrgentTodayChanged,
                label = "오늘 바로 결정해야 한다고 느끼고 있음",
                tag = "urgent-today",
            )
        }
    }
}

@Composable
private fun BinaryCheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    tag: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TransactionPrecheckPanel(estimate: RiskTransactionEstimate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction-precheck"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "위험거래 감지: ${estimate.precheckGrade.label}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = estimate.headline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            estimate.projectedPressureWon?.let { pressure ->
                Text(
                    text = "${estimate.pressureLabel}: ${pressure.toKoreanWon()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            estimate.warnings.forEach { warning ->
                Text(
                    text = "- $warning",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun InterviewScreen(
    scenario: RiskScenario,
    answers: Map<String, String>,
    canSubmit: Boolean,
    onAnswerChanged: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            InterviewerPanel(
                title = "AI 면접: ${scenario.title}",
                message = "정답을 고르는 화면이 아닙니다. 제가 묻는 내용을 본인 말로 답해 주세요. 답변 속 오해 가능성과 손실 감내 능력을 함께 확인하겠습니다.",
            )
        }
        items(scenario.questions, key = { it.id }) { question ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                InterviewerQuestionBubble(prompt = question.prompt)
                OutlinedTextField(
                    value = answers[question.id].orEmpty(),
                    onValueChange = { onAnswerChanged(question.id, it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("answer-${question.id}"),
                    label = { Text("답변") },
                    supportingText = { Text(question.helperText) },
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp),
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("설명 다시 보기")
                }
                Button(
                    onClick = onSubmit,
                    enabled = canSubmit,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("submit-interview"),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("결과 보기")
                }
            }
        }
    }
}

@Composable
private fun ResultScreen(
    scenario: RiskScenario,
    result: RiskInterviewResult,
    onRestart: () -> Unit,
    onReviseAnswers: () -> Unit,
    onEditTransactionContext: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("result-report"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            InterviewerPanel(
                title = "면접 결과: ${result.grade.label}",
                message = result.summary,
            )
        }
        item {
            GradePanel(result = result)
        }
        item {
            TextListBlock(
                title = "이해한 내용",
                values = result.understoodConcepts.ifEmpty { listOf("충분히 확인된 핵심 개념이 아직 없습니다.") },
            )
        }
        item {
            TextListBlock(
                title = "거래 조건 점검",
                values = result.contextWarnings.ifEmpty { listOf("거래 조건상 즉시 가중된 위험은 감지되지 않았습니다.") },
            )
        }
        item {
            TextListBlock(
                title = "점검된 위험",
                values = result.riskFactors,
            )
        }
        item {
            TextListBlock(
                title = "권장 행동",
                values = result.recommendedActions,
            )
        }
        item {
            TextListBlock(
                title = "재질문 포인트",
                values = result.followUpQuestions,
            )
        }
        item {
            TextListBlock(
                title = "대안 행동",
                values = result.saferAlternatives,
            )
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = result.coolingOffNotice,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            Text(
                text = "이 결과는 금융 의사결정을 돕는 이해도 점검이며, 법적 금융자문이나 투자 권유가 아닙니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    OutlinedButton(
                        onClick = onReviseAnswers,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("답변 수정")
                    }
                    OutlinedButton(
                        onClick = onEditTransactionContext,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text("조건 수정")
                    }
                }
                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("다른 거래 점검")
                }
            }
        }
    }
}

@Composable
private fun GradePanel(result: RiskInterviewResult) {
    val background = when (result.grade) {
        RiskGrade.SAFE -> MaterialTheme.colorScheme.primary
        RiskGrade.CAUTION -> MaterialTheme.colorScheme.secondary
        RiskGrade.RISK -> MaterialTheme.colorScheme.error
        RiskGrade.HIGH_RISK -> MaterialTheme.colorScheme.error
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = background,
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = result.grade.label,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = result.misunderstoodConcepts.joinToString(separator = " / ").ifBlank { "중대한 오해는 감지되지 않았습니다." },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${result.grade.aiAction} · ${result.grade.userOutcome}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun HeaderBlock(
    eyebrow: String,
    title: String,
    body: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RiskChips(risks: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        risks.forEach { risk ->
            AssistChip(
                onClick = {},
                label = { Text(risk) },
                shape = RoundedCornerShape(8.dp),
            )
        }
    }
}

@Composable
private fun TextListBlock(
    title: String,
    values: List<String>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        values.forEach { value ->
            Text(
                text = "- $value",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
