# FinGuard AI PRD

## Summary

FinGuard AI is an AI-style financial-risk understanding interviewer for the FIN:NECT challenge. It intervenes immediately before risky financial actions such as revolving credit, card loans, high-risk crypto purchases, or surging-stock investments. The goal is not to block users, but to help them choose after understanding the risk.

## Problem

Financial apps often guide users quickly toward loans, revolving credit, or investments. Existing warnings and consent checkboxes do not verify whether the user understands core risks such as deferred payment, fees, total repayment, credit-score impact, volatility, or principal loss.

Primary target users:

- People in their 20s starting independent financial life.
- Older adults with lower digital-finance familiarity.
- Beginner investors.
- Repeated users of revolving credit, card loans, or cash advances.

## MVP Goal

Build an Android MVP that simulates the pre-transaction safety check:

- Let the user select one risky financial scenario.
- Show an animated human-style AI interviewer to make the flow feel like an interview, not a static checklist.
- Explain the risk in plain Korean.
- Capture basic transaction conditions such as amount, affordable repayment/loss, essential-money usage, and urgency.
- Show a pre-interview risk detection panel based on those transaction conditions.
- Ask interview questions requiring free-form answers.
- Analyze answers with a local rule-based model.
- Produce a risk grade, AI action, cooling-off notice, and practical next actions.

## Scenarios

- Revolving credit: user may think the monthly card bill disappears rather than being deferred with fees.
- Card loan: user may underestimate total repayment, interest burden, overdue penalties, and credit-score impact.
- Crypto: user may focus on short-term gains while ignoring volatility and principal-loss risk.
- Surging stock: user may buy because of hype without checking downside or loss tolerance.

## Risk Grades

| Grade | Meaning | App action |
| --- | --- | --- |
| `안전` | User understands core risks | Show summary report and allow proceeding |
| `주의` | User partly misunderstands risk | Explain the weak point and ask for reconsideration |
| `위험` | User misses a core risk | Recommend reconsideration and cooling-off |
| `고위험` | User misunderstands risk and cannot tolerate repayment/loss burden | Recommend delay, counseling, or safer alternatives |

## Core User Experience

1. User chooses a scenario.
2. App shows a short risk explanation using beginner-friendly Korean.
3. App asks scenario-specific interview questions.
4. User answers in free text.
5. App analyzes answer text and selected transaction context.
6. App shows grade, reasons, misunderstood concepts, and safer actions.

## Functional Requirements

- Scenario cards for the four MVP scenarios.
- Local animated human-style interviewer in explanation, interview, and result screens.
- Interviewer animation includes speaking mouth movement, blinking, subtle eye movement, body motion, and hand gestures.
- Scenario-specific explanations and interview questions.
- Transaction-condition form before the interview.
- Projected pressure estimate for repayment burden or 30% investment loss.
- Free-form answer input.
- Local deterministic analyzer implementing `RiskInterviewAnalyzer`.
- Report showing:
  - risk grade,
  - AI action and user outcome,
  - short explanation,
  - understood concepts,
  - detected misunderstanding,
  - transaction-condition warnings,
  - follow-up questions,
  - safer alternatives,
  - cooling-off notice,
  - recommended next action,
  - non-advice disclaimer.

## Non-Goals

- No real bank/card/securities API integration.
- No real transaction execution or blocking.
- No Android-side OpenAI API key.
- No personal financial-data storage.
- No personalized legal, investment, or credit advice.

## Success Criteria

- A beginner can complete a scenario-to-report flow without instructions.
- Risk grades are explainable from the user's answers.
- The MVP can later swap the local analyzer for a secure server-backed LLM analyzer.
- `./gradlew test`, `./gradlew assembleDebug`, and Android test APK compilation pass in the target build environment.
