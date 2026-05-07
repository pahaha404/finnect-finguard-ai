# Codex Cloud Task: Implement FinGuard AI Android MVP

Implement the FinGuard AI MVP in this repository.

Hard requirements:

- Build a native Android app with Kotlin and Jetpack Compose.
- Use the PDF-derived requirements in `docs/PRD.md` and workflow in `docs/WORKFLOW.md`.
- Do not put OpenAI or LLM API keys in the Android app.
- Start with a deterministic local analyzer.
- Define a `RiskInterviewAnalyzer` interface so a secure server-backed LLM analyzer can replace the local analyzer later.

MVP behavior:

- Four scenarios: revolving credit, card loan, crypto, surging stock.
- Scenario selection screen.
- Plain Korean risk explanation screen.
- Interview screen with free-form answer input.
- Result report screen with grade, reasons, misunderstood concepts, recommended actions, and non-advice disclaimer.
- Risk grades exactly: `안전`, `주의`, `위험`, `고위험`.

Minimum analyzer behavior:

- Revolving-credit misunderstanding such as "card payment disappears" grades at least `위험`.
- Investment answer saying a 30% loss would harm living expenses or loan repayment grades `고위험`.
- Correctly explaining the core concept grades `안전` or `주의`.

Expected technical shape:

- Gradle-based Android project.
- Kotlin domain models for scenarios, interview questions, answers, and report results.
- Jetpack Compose UI using Material 3.
- ViewModel or equivalent UI-state owner for the scenario-to-report flow.
- Unit tests for analyzer rules.
- UI smoke test for scenario selection to report.

Verification:

- Run `./gradlew test`.
- Run `./gradlew assembleDebug`.
- Update `README.md`, `AGENTS.md`, `docs/PRD.md`, and `docs/WORKFLOW.md` if implementation details differ from the current plan.
