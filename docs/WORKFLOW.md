# FinGuard AI Workflow

## User Workflow

1. Select scenario
   - Revolving credit, card loan, crypto, or surging stock.

2. Read plain-language risk explanation
   - What the product/action does.
   - What can go wrong.
   - Which cost, repayment, or loss risks matter.

3. Enter transaction conditions
   - Planned amount.
   - Affordable repayment or loss amount.
   - Whether essential living or debt-repayment money is included.
   - Whether the user feels pressure to decide today.
   - App shows a pre-interview risk detection panel.

4. Answer interview questions
   - User writes answers in their own words.
   - Questions check understanding and loss/repayment tolerance.

5. Receive risk report
   - Grade: `안전`, `주의`, `위험`, or `고위험`.
   - AI action and user outcome.
   - Why this grade was assigned.
   - Which concepts the user appears to understand.
   - What concept may be misunderstood.
   - Which transaction condition increased or reduced risk.
   - Follow-up questions and cooling-off notice.
   - Safer next action.

6. Decide outside the app
   - The MVP does not execute or block real financial transactions.

## App Workflow

1. Load scenario definitions.
2. Render scenario selection UI.
3. Render risk explanation for selected scenario.
4. Collect transaction conditions.
5. Render transaction precheck.
6. Collect free-form answers.
7. Pass transaction context and answers to `RiskInterviewAnalyzer`.
8. Render `RiskInterviewResult`.

## Analyzer Workflow

1. Normalize Korean answer text.
2. Check scenario-specific misunderstanding signals.
3. Check core-risk understanding signals.
4. Check repayment/loss burden signals.
5. Check transaction amount against affordable repayment/loss.
6. Produce grade, AI action, follow-up questions, cooling-off notice, and alternatives.

Suggested deterministic rules:

- Revolving-credit answer says the bill disappears or is free: at least `위험`.
- Card-loan answer ignores repayment, interest, overdue penalty, and credit impact: at least `위험`.
- Investment answer says 30% loss would affect living expenses, debt repayment, or rent: `고위험`.
- Transaction context using essential money or exceeding affordable loss/payment increases grade severity.
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

Current status:

- The repo has been created and pushed as `pahaha404/finnect-finguard-ai`.
- Codex CLI authentication works when run outside the sandbox.
- A Codex Cloud environment for `pahaha404/finnect-finguard-ai` was not found, so the MVP was implemented locally after user approval.

## Failure Handling

- If Codex CLI is not logged in, stop and run `codex login`.
- If GitHub CLI token is invalid, stop and run `gh auth login -h github.com`.
- If no cloud environment exists, create one in ChatGPT Codex settings before implementation.
- If Android SDK is missing in Cloud, update environment setup and retry the Cloud task.
- If the user explicitly approves local fallback, use the local Android SDK and Gradle wrapper, then record the Cloud blocker in `README.md`.
