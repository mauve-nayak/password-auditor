# Personal Password Strength Auditor

A small, fully-offline Java command-line tool that reads a list of passwords
from a local file and tells you how strong each one really is. For every
password it reports the length, the character classes it uses, any weak
patterns it contains (common passwords, keyboard walks, repeating characters,
sequences, years, dictionary words), an entropy estimate, and a rough
crack-time. Nothing is ever sent over the network — the whole analysis happens
inside a single Java process on your own machine.

The project was built as a learning exercise covering string analysis, regular
expressions, simple entropy math, and good password hygiene.

---

## Features

- **Length & character-class diversity** — flags lowercase, uppercase, digits
  and symbols, and counts how many of the four classes a password actually uses.
- **Pattern detection** — catches common/leaked passwords, repeating characters
  (`aaaa`), sequential digits (`1234`), sequential letters (`abcd`), QWERTY
  keyboard walks (`qwerty`, `asdf`), years (`1990`–`2099`) and dictionary words.
- **Entropy estimation** — uses the simplified Shannon formula
  `entropy = length × log2(alphabetSize)`.
- **Crack-time estimation** — converts entropy into a human-readable time
  (seconds → minutes → hours → days → years) assuming an offline attacker at
  10 billion guesses/second.
- **Strength verdict** — maps the crack time to a Very Weak / Weak / Fair /
  Strong / Very Strong bucket.
- **Two report formats** — a readable console table and a self-contained HTML
  page you can save and share.
- **Privacy first** — no network calls, no telemetry. Passwords are masked in
  all output so the reports are safe to screenshot.

---

## Tech Stack

| Layer        | Choice                                   |
|--------------|------------------------------------------|
| Language     | Java 17 (LTS)                            |
| Build tool   | Maven                                    |
| Testing      | JUnit 5                                  |
| Paradigm     | Object-oriented, modular, dependency-free|
| External libs| None (standard library only)             |

---

## Project Structure

```
password-auditor/
├── README.md
├── statement.md
├── pom.xml
├── .gitignore
├── data/
│   ├── common_passwords.txt     # list of leaked/common passwords to flag
│   ├── dictionary_words.txt     # ordinary words to flag
│   └── sample_passwords.txt     # demo input you can try the tool on
├── docs/
│   └── *.png                    # design diagrams and screenshots
└── src/
    ├── main/java/com/passauditor/
    │   ├── Main.java                 # CLI entry point
    │   ├── config/Config.java        # tunable constants
    │   ├── model/
    │   │   ├── PasswordReport.java   # immutable report object (Builder)
    │   │   └── PasswordStrength.java # strength enum
    │   ├── analyzer/
    │   │   ├── CharacterClassAnalyzer.java
    │   │   ├── PatternDetector.java
    │   │   └── PasswordAnalyzer.java   # orchestrates the pipeline
    │   ├── entropy/
    │   │   ├── EntropyCalculator.java
    │   │   └── CrackTimeEstimator.java
    │   ├── input/PasswordFileReader.java
    │   └── reporting/
    │       ├── ConsoleReporter.java
    │       └── HtmlReporter.java
    └── test/java/com/passauditor/
        ├── analyzer/{CharacterClassAnalyzerTest,
        │              PatternDetectorTest,
        │              PasswordAnalyzerTest}.java
        ├── entropy/EntropyCalculatorTest.java
        └── input/PasswordFileReaderTest.java
```

---

## How to Run

### Prerequisites
- Java 17 or newer (`java -version`)
- Maven 3.6+ (`mvn -version`)

### 1. Build the project
```bash
mvn clean package
```
This compiles the code, runs the tests, and produces
`target/password-auditor-1.0.0.jar`.

### 2. Run the auditor
```bash
# Console report only
java -jar target/password-auditor-1.0.0.jar data/sample_passwords.txt

# Console report + an HTML report you can open in a browser
java -jar target/password-auditor-1.0.0.jar data/sample_passwords.txt --html audit.html
```

### 3. Use your own password list
Create a text file with **one password per line**, then point the tool at it:
```bash
java -jar target/password-auditor-1.0.0.jar my_passwords.txt --html my_report.html
```

> The tool masks passwords in all output (only the first and last character are
> shown), so the console and HTML reports are safe to share.

---

## Testing

The project ships with JUnit 5 unit tests covering every module:

| Test class                      | What it covers                                  |
|---------------------------------|-------------------------------------------------|
| CharacterClassAnalyzerTest      | class detection, alphabet size, null safety    |
| PatternDetectorTest             | every pattern type + clean-password case        |
| EntropyCalculatorTest           | entropy math, class-diversity ordering          |
| CrackTimeEstimatorTest          | crack-time scaling, human-readable formatting   |
| PasswordFileReaderTest          | trimming, missing files, binary-file guard      |
| PasswordAnalyzerTest            | end-to-end pipeline, immutability, null safety   |

Run them all with:
```bash
mvn test
```

---

## How the Math Works

For a password of length `L` drawing from an alphabet of size `A`, the raw
entropy in bits is:

```
entropy = L × log2(A)
```

where `A` is the sum of the classes present (lowercase 26, uppercase 26,
digits 10, symbols 33). The estimated average time to crack is:

```
crackTime = 2^(entropy − 1) / guessesPerSecond
```

using an offline attack rate of 10¹⁰ guesses/second. The `−1` accounts for
finding the password on average halfway through the search space.

> This is a **simplified textbook estimate**. Real attackers use dictionaries and
> patterns, which is why the PatternDetector runs alongside the math — a
> password can have a high raw entropy yet still be dangerous if it is built
> from predictable pieces.

---

## Screenshots

Screenshots of the console output and the HTML report live in `docs/`.

---

## Limitations & Future Work

- Entropy is a simplified estimate, not a full zxcvbn-style model.
- The common-password and dictionary lists are small starter sets; drop bigger
  lists into `data/` to improve coverage.
- A GUI version and per-character heat-map visualisation are planned.

---

## License

This project is released for educational use. Feel free to adapt it for your own
learning.
