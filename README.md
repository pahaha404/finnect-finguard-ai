# FinGuard AI

FinGuard AI is a FIN:NECT challenge MVP for protecting financial consumers before risky transactions. The service acts as an AI financial-risk interviewer: it explains the product in plain language, asks the user to answer risk-check questions, classifies the understanding level, and recommends safer next actions.

The initial product target is a native Android app built with Kotlin and Jetpack Compose. The MVP must not embed an OpenAI API key in the Android client. It should start with a local rule-based analyzer and keep LLM integration behind an interface that can later be backed by a secure server or B2B API.

## Source Material

- `FINNECT_FinGuard_AI_Overview.pdf`: original FIN:NECT idea overview.
- `docs/PRD.md`: product requirements derived from the PDF.
- `docs/WORKFLOW.md`: user, app, and Codex Cloud workflows.
- `docs/CODEX_CLOUD_TASK.md`: prompt to submit after Codex Cloud is ready.

## Implemented MVP

- Scenario selection: revolving credit, card loan, crypto, and surging stock.
- Animated human-style AI interviewer shown in the explanation, interview, and result flow.
- Mouth movement, blinking, eye tracking, light body motion, and hand gestures are rendered natively in Compose.
- Plain-language risk explanation before each decision.
- Transaction-condition input: amount, affordable repayment/loss, essential-money usage, and urgency.
- Pre-interview risk detection with projected repayment or 30% loss pressure.
- Interview-style question and answer flow.
- Understanding/risk classification: `안전`, `주의`, `위험`, `고위험`.
- Result report with understood concepts, misunderstood concepts, transaction warnings, follow-up questions, cooling-off notice, and alternative actions.

## Android Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM with UI state exposed from ViewModels
- Pure Kotlin domain module/classes for scenario, answer analysis, and risk grading
- Unit tests for analyzer behavior
- Compose UI smoke test for the main scenario-to-report flow

## Project Layout

- `app/src/main/java/com/finnect/finguard/domain`: scenarios, transaction precheck, interview models, `RiskInterviewAnalyzer`, and local deterministic analyzer.
- `app/src/main/java/com/finnect/finguard/ui`: Compose screens and UI state owner.
- `app/src/main/java/com/finnect/finguard/ui/FinGuardApp.kt`: Compose screens, including the animated interviewer avatar.
- `app/src/test`: analyzer unit tests.
- `app/src/androidTest`: Compose smoke test.

## Codex Cloud Status

This repository was pushed to the private GitHub repo `pahaha404/finnect-finguard-ai`. Codex CLI login works, but the Cloud environment for this new repo was not found when tested with:

```powershell
codex cloud exec --env 'pahaha404/finnect-finguard-ai' --branch main '...'
```

The MVP was therefore implemented locally after explicit user direction. To move future work back to Codex Cloud, create a Codex Cloud environment for the GitHub repo in ChatGPT Codex settings, then submit `docs/CODEX_CLOUD_TASK.md`.

Required setup:

1. Authenticate the local Codex CLI with ChatGPT:

   ```powershell
   codex login
   ```

   If PowerShell blocks `codex.ps1`, run the CLI through Node:

   ```powershell
   & 'C:\Users\1\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin\node.exe' 'C:\Users\1\AppData\Roaming\npm\node_modules\@openai\codex\bin\codex.js' login
   ```

2. Re-authenticate GitHub CLI:

   ```powershell
   gh auth login -h github.com
   ```

3. If needed, create and push the private GitHub repo:

   ```powershell
   gh repo create finnect-finguard-ai --private --source . --remote origin --push
   ```

4. In ChatGPT Codex settings, create a cloud environment for that GitHub repo.

5. Submit the cloud task:

   ```powershell
   codex cloud exec --env <ENV_ID> --branch main "$(Get-Content -Raw docs/CODEX_CLOUD_TASK.md)"
   ```

## Verification

On Windows, this project was verified with:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME='C:\Users\1\AppData\Local\Android\Sdk'
$env:ANDROID_SDK_ROOT='C:\Users\1\AppData\Local\Android\Sdk'
$env:GRADLE_USER_HOME='C:\Users\1\.gradle'
.\gradlew.bat test --no-daemon
.\gradlew.bat assembleDebug --no-daemon
.\gradlew.bat assembleDebugAndroidTest --no-daemon
```

On Codex Cloud or Linux:

```bash
./gradlew test
./gradlew assembleDebug
```
