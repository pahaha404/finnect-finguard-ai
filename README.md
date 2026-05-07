# FinGuard AI

FinGuard AI is a FIN:NECT challenge MVP for protecting financial consumers before risky transactions. The service acts as an AI financial-risk interviewer: it explains the product in plain language, asks the user to answer risk-check questions, classifies the understanding level, and recommends safer next actions.

The initial product target is a native Android app built with Kotlin and Jetpack Compose. The MVP must not embed an OpenAI API key in the Android client. It should start with a local rule-based analyzer and keep LLM integration behind an interface that can later be backed by a secure server or B2B API.

## Source Material

- `FINNECT_FinGuard_AI_Overview.pdf`: original FIN:NECT idea overview.
- `docs/PRD.md`: product requirements derived from the PDF.
- `docs/WORKFLOW.md`: user, app, and Codex Cloud workflows.
- `docs/CODEX_CLOUD_TASK.md`: prompt to submit after Codex Cloud is ready.

## Target MVP

- Scenario selection: revolving credit, card loan, crypto, and surging stock.
- Plain-language risk explanation before each decision.
- Interview-style question and answer flow.
- Understanding/risk classification: `안전`, `주의`, `위험`, `고위험`.
- Result report with misunderstood concepts, risk factors, and alternative actions.

## Recommended Android Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM with UI state exposed from ViewModels
- Pure Kotlin domain module/classes for scenario, answer analysis, and risk grading
- Unit tests for analyzer behavior
- Compose UI smoke test for the main scenario-to-report flow

## Codex Cloud First

This repository is prepared for Codex Cloud execution. Do not implement the Android MVP locally until the repository is connected to a Codex Cloud environment.

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

3. Create a private GitHub repo and push this folder:

   ```powershell
   gh repo create finnect-finguard-ai --private --source . --remote origin --push
   ```

4. In ChatGPT Codex settings, create a cloud environment for that GitHub repo.

5. Submit the cloud task:

   ```powershell
   codex cloud exec --env <ENV_ID> --branch main "$(Get-Content -Raw docs/CODEX_CLOUD_TASK.md)"
   ```

## Verification

Once the Cloud task creates the Android project, expected verification commands are:

```bash
./gradlew test
./gradlew assembleDebug
```
