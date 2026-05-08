# AGENTS.md

## Project Goal

Build FinGuard AI as a Kotlin/Jetpack Compose Android MVP based on `FINNECT_FinGuard_AI_Overview.pdf`.

FinGuard AI is not a blocking service. It helps users understand risky financial transactions before they choose to proceed.

## Execution Rule

Prefer Codex Cloud for substantial future changes after this repository is connected to a GitHub-backed Codex Cloud environment. If Cloud is unavailable, report the blocker and exact remediation steps. Local implementation is acceptable only when the user explicitly asks to proceed locally.

## Product Rules

- Cover four MVP scenarios: revolving credit, card loan, crypto, and surging stock.
- Use plain Korean explanations suitable for financial beginners.
- Collect transaction conditions before the interview and include them in grading.
- Classify results into exactly: `안전`, `주의`, `위험`, `고위험`.
- The UX must protect user autonomy: recommend reconsideration, cooling-off, counseling, or safer alternatives without shaming the user.
- Do not represent the app as official financial advice.
- Do not store sensitive personal financial data in the MVP.

## Architecture Rules

- Use Kotlin and Jetpack Compose for the Android app.
- Do not put OpenAI or LLM API keys in the Android client.
- Start with a local rule-based analyzer.
- Define a `RiskInterviewAnalyzer` interface so a future server-backed LLM analyzer can replace the local implementation.
- Keep scenario definitions, questions, grading rules, and result recommendations in domain-layer code or structured resources.
- Keep UI state deterministic and testable.

## Expected Screens

- Scenario selection screen.
- Scenario risk explanation screen.
- Interview screen with question prompts and free-form answers.
- Result report screen with risk grade, reasoning summary, misunderstood concepts, and recommended actions.

## Test Requirements

Run and keep passing:

```bash
./gradlew test
./gradlew assembleDebug
```

On this Windows workstation, use `gradlew.bat` with `JAVA_HOME` set to Android Studio JBR and `ANDROID_HOME` set to `C:\Users\1\AppData\Local\Android\Sdk`.

Minimum domain tests:

- Revolving-credit misunderstanding such as "card payment disappears" grades at least `위험`.
- Investment answer saying a 30% loss would harm living expenses or loan repayment grades `고위험`.
- Correctly explaining the core concept grades `안전` or `주의`.

Minimum UI test:

- User can select a scenario, answer interview questions, and reach a visible result report.

## Documentation Requirements

Keep these files current:

- `README.md`: project overview, setup, execution, verification.
- `docs/PRD.md`: product requirements and MVP scope.
- `docs/WORKFLOW.md`: product workflow and Codex Cloud workflow.

## Implementation Constraints

- Prefer simple, explicit domain models over premature abstraction.
- Keep financial-risk language conservative and easy to understand.
- Do not add backend, authentication, analytics, or persistent storage unless required for the MVP flow.
- Avoid unrelated formatting churn.
