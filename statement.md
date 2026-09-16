# Problem Statement

## The problem

Most people still reuse the same handful of passwords across dozens of
accounts, and the passwords they choose tend to be predictable — names,
birthdays, keyboard walks, or slight variations of the word "password". When a
breach happens, attackers exploit exactly this predictability: they don't try
every possible string, they try the most likely ones first.

The trouble is that users have almost no way to *see* how weak a password
really is. Online "password strength meters" are often just a coloured bar with
no explanation, and — more importantly — typing your real password into a
random website is itself a security risk. There is a real need for a tool that
audits a batch of passwords *locally*, with full transparency about what it
found and why a password scored the way it did.

## Scope

The Personal Password Strength Auditor is a **fully offline** command-line tool.
Given a local text file containing one password per line, it produces a
per-password report covering:

- length,
- character-class diversity (lowercase, uppercase, digits, symbols),
- weak-pattern matches (common/leaked passwords, repeating characters,
  sequential digits and letters, keyboard walks, years, dictionary words),
- an entropy estimate (in bits),
- an estimated crack-time,
- a qualitative strength verdict.

It deliberately does **not** store, transmit, or persist any password. The only
side effects are the console output and an optional HTML file written to disk.
The project uses no external libraries and no database.

## Target users

- **Everyday users** who want to check a batch of passwords before adopting a
  password manager, without trusting a third-party website.
- **Students and educators** using it as a teaching aid for string analysis,
  regular expressions, simple entropy math, and security hygiene.
- **Developers** who want a lightweight, dependency-free reference for how
  password-strength estimation works under the hood.

## Objectives

1. Analyse passwords purely locally — zero network access, zero telemetry.
2. Give actionable, human-readable feedback (not just a score) so users learn
   *why* a password is weak.
3. Combine mathematical entropy estimation with pattern-based detection, since
   either alone is misleading.
4. Keep the codebase modular and testable, with each concern in its own module.

## High-level features

| # | Feature | Module |
|---|---------|--------|
| F1 | Read passwords from a local file (one per line) | `input` |
| F2 | Measure length and character-class diversity | `analyzer` |
| F3 | Detect weak patterns (common passwords, sequences, keyboard walks, years, dictionary words) | `analyzer` |
| F4 | Estimate entropy using a simplified Shannon formula | `entropy` |
| F5 | Estimate crack-time and assign a strength verdict | `entropy`, `model` |
| F6 | Render a console report with a summary | `reporting` |
| F7 | Render a self-contained HTML report | `reporting` |
| F8 | Mask passwords in all output for safe sharing | `reporting` |

## Out of scope

- Storing or syncing passwords (this is not a password manager).
- Any network communication or breach-database lookup.
- A graphical user interface (a CLI is sufficient for v1).
- A full zxcvbn-style probabilistic model.
