package com.passauditor.analyzer;

import com.passauditor.config.Config;
import com.passauditor.entropy.CrackTimeEstimator;
import com.passauditor.entropy.EntropyCalculator;
import com.passauditor.model.PasswordReport;

import java.util.Set;

/**
 * Orchestrates the full analysis pipeline for a single password.
 *
 * <p>This is the central functional module that ties together character-class
 * analysis, pattern detection, entropy calculation and crack-time estimation.
 * Keeping the orchestration in one place means reporters and the CLI never need
 * to know about the individual analyser internals.
 */
public class PasswordAnalyzer {

    private final PatternDetector patternDetector;
    private final CrackTimeEstimator crackTimeEstimator;

    public PasswordAnalyzer(Set<String> commonPasswords, Set<String> dictionaryWords) {
        this.patternDetector = new PatternDetector(commonPasswords, dictionaryWords);
        this.crackTimeEstimator = new CrackTimeEstimator();
    }

    /**
     * Analyses a single password and returns a complete {@link PasswordReport}.
     */
    public PasswordReport analyze(String password) {
        if (password == null) {
            password = "";
        }

        boolean hasLower = CharacterClassAnalyzer.hasLower(password);
        boolean hasUpper = CharacterClassAnalyzer.hasUpper(password);
        boolean hasDigit = CharacterClassAnalyzer.hasDigit(password);
        boolean hasSymbol = CharacterClassAnalyzer.hasSymbol(password);
        int classCount = CharacterClassAnalyzer.classCount(password);

        double entropy = EntropyCalculator.calculate(password);
        double seconds = crackTimeEstimator.seconds(entropy);

        PasswordReport.Builder builder = new PasswordReport.Builder()
                .password(password)
                .length(password.length())
                .hasLower(hasLower)
                .hasUpper(hasUpper)
                .hasDigit(hasDigit)
                .hasSymbol(hasSymbol)
                .charClassCount(classCount)
                .entropyBits(entropy)
                .crackTimeSeconds(seconds);

        // Pattern-based warnings.
        for (String pattern : patternDetector.detect(password)) {
            builder.addPattern(pattern);
        }

        // Hygiene warnings independent of patterns.
        if (password.length() < Config.MIN_RECOMMENDED_LENGTH) {
            builder.addWarning("Shorter than " + Config.MIN_RECOMMENDED_LENGTH + " characters");
        }
        if (classCount < 3) {
            builder.addWarning("Uses fewer than 3 character classes");
        }

        return builder.build();
    }
}
