package com.finnect.finguard.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.finnect.finguard.domain.RiskGrade
import com.finnect.finguard.domain.RiskInterviewResult
import com.finnect.finguard.domain.RiskScenario

@Composable
fun FinGuardApp(viewModel: FinGuardViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FinGuardAppContent(
        state = state,
        onScenarioSelected = viewModel::selectScenario,
        onStartInterview = viewModel::startInterview,
        onAnswerChanged = viewModel::updateAnswer,
        onSubmit = viewModel::submitInterview,
        onBackToScenarios = viewModel::backToScenarios,
        onBackToExplanation = viewModel::backToExplanation,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FinGuardAppContent(
    state: FinGuardUiState,
    onScenarioSelected: (RiskScenario) -> Unit,
    onStartInterview: () -> Unit,
    onAnswerChanged: (String, String) -> Unit,
    onSubmit: () -> Unit,
    onBackToScenarios: () -> Unit,
    onBackToExplanation: () -> Unit,
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
                            Icon(Icons.Default.ArrowBack, contentDescription = "처음으로")
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
                        )
                    }
                }
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
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ExplanationScreen(
    scenario: RiskScenario,
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
            RiskChips(risks = scenario.keyRisks)
        }
        item {
            Button(
                onClick = onStartInterview,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start-interview"),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Default.Shield, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("이해도 면접 시작")
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
            HeaderBlock(
                eyebrow = "AI 면접",
                title = scenario.title,
                body = "정답을 고르는 화면이 아닙니다. 이해한 내용을 본인 말로 적으면 앱이 오해 가능성을 점검합니다.",
            )
        }
        items(scenario.questions, key = { it.id }) { question ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = question.prompt,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
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
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
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
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("result-report"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            HeaderBlock(
                eyebrow = scenario.title,
                title = "위험등급 ${result.grade.label}",
                body = result.summary,
            )
        }
        item {
            GradePanel(result = result)
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
            Text(
                text = "이 결과는 금융 의사결정을 돕는 이해도 점검이며, 법적 금융자문이나 투자 권유가 아닙니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
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
