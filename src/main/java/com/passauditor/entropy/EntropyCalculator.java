package com.passauditor.entropy;

import com.passauditor.analyzer.CharacterClassAnalyzer;

/**
 * Estimates the entropy of a password in bits using the simplified Shannon-style
 * formula:
 *
 * <pre>
 *   entropy = length * log2(alphabetSize)
 * </pre>
 *
 * <p>This is intentionally a textbook estimate. Real-world attackers use
 * dictionaries and patterns, which is why the {@code PatternDetector} runs
 * alongside this calculator - a password can have a high raw entropy yet still
 * be dangerous if it is built from predictable patterns.
 */
public final class EntropyCalculator {

    private EntropyCalculator() {
    }

    /**
     * Computes the raw entropy in bits for the given password.
     *
     * @param password the password to score (null treated as empty)
     * @return entropy in bits, 0 if the password is empty or has no recognised
     *         character class
     */
    public static double calculate(String password) {
        if (password == null || password.isEmpty()) {
            return 0.0;
        }
        int alphabet = CharacterClassAnalyzer.alphabetSize(password);
        if (alphabet <= 1) {
            return 0.0;
        }
        return password.length() * (Math.log(alphabet) / Math.log(2));
    }
}
