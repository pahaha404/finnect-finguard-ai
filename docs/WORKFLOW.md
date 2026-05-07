# FinGuard AI Workflow

## User Workflow

1. Select scenario
   - Revolving credit, card loan, crypto, or surging stock.

2. Read plain-language risk explanation
   - What the product/action does.
   - What can go wrong.
   - Which cost, repayment, or loss risks matter.

3. Answer interview questions
   - User writes answers in their own words.
   - Questions check understanding and loss/repayment tolerance.

4. Receive risk report
   - Grade: `안전`, `주의`, `위험`, or `고위험`.
   - Why this grade was assigned.
   - What concept may be misunderstood.
   - Safer next action.

5. Decide outside the app
   - The MVP does not execute or block real financial transactions.

## App Workflow

1. Load scenario definitions.
2. Render scenario selection UI.
3. Render risk explanation for selected scenario.
4. Collect free-form answers.
5. Pass answers to `RiskInterviewAnalyzer`.
6. Render `RiskInterviewResult`.

## Analyzer Workflow

1. Normalize Korean answer text.
2. Check scenario-specific misunderstanding signals.
3. Check core-risk understanding signals.
4. Check repayment/loss burden signals.
5. Produce grade and explanation.

Suggested deterministic rules:

- Revolving-credit answer says the bill disappears or is free: at least `위험`.
- Card-loan answer ignores repayment, interest, overdue penalty, and credit impact: at least `위험`.
- Investment answer says 30% loss would affect living expenses, debt repayment, or rent: `고위험`.
- Answer correctly states deferral/fee or principal-loss risk: `안전` or `주의` depending on missing details.

## Codex Cloud Workflow

1. Prepare GitHub repo
   - Initialize repo locally.
   - Push to private GitHub repo `finnect-finguard-ai`.

2. Prepare Codex Cloud
   - Sign in to Codex CLI.
   - Connect GitHub account in ChatGPT Codex settings.
   - Create a cloud environment for the repo.
   - Ensure Android build tooling works in setup.

3. Submit implementation task
   - Use `docs/CODEX_CLOUD_TASK.md` as the Cloud task prompt.
   - Target the `main` branch.

4. Verify Cloud result
   - Inspect diff.
   - Run `./gradlew test`.
   - Run `./gradlew assembleDebug`.
   - Apply Cloud diff locally only after review.

## Failure Handling

- If Codex CLI is not logged in, stop and run `codex login`.
- If GitHub CLI token is invalid, stop and run `gh auth login -h github.com`.
- If no cloud environment exists, create one in ChatGPT Codex settings before implementation.
- If Android SDK is missing in Cloud, update environment setup and retry the Cloud task.
