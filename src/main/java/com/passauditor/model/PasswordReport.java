package com.passauditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable report object capturing everything we learned about a single
 * password. It is produced by the analyser pipeline and consumed by the
 * reporters, which keeps the analysis layer cleanly separated from any
 * output format.
 */
public class PasswordReport {

    private final String password;
    private final int length;
    private final boolean hasLower;
    private final boolean hasUpper;
    private final boolean hasDigit;
    private final boolean hasSymbol;
    private final int charClassCount;
    private final double entropyBits;
    private final double crackTimeSeconds;
    private final PasswordStrength strength;
    private final List<String> matchedPatterns;
    private final List<String> warnings;

    private PasswordReport(Builder b) {
        this.password = b.password;
        this.length = b.length;
        this.hasLower = b.hasLower;
        this.hasUpper = b.hasUpper;
        this.hasDigit = b.hasDigit;
        this.hasSymbol = b.hasSymbol;
        this.charClassCount = b.charClassCount;
        this.entropyBits = b.entropyBits;
        this.crackTimeSeconds = b.crackTimeSeconds;
        this.strength = b.strength;
        this.matchedPatterns = Collections.unmodifiableList(new ArrayList<>(b.matchedPatterns));
        this.warnings = Collections.unmodifiableList(new ArrayList<>(b.warnings));
    }

    // --- accessors ---------------------------------------------------------

    public String getPassword() {
        return password;
    }

    public int getLength() {
        return length;
    }

    public boolean hasLower() {
        return hasLower;
    }

    public boolean hasUpper() {
        return hasUpper;
    }

    public boolean hasDigit() {
        return hasDigit;
    }

    public boolean hasSymbol() {
        return hasSymbol;
    }

    public int getCharClassCount() {
        return charClassCount;
    }

    public double getEntropyBits() {
        return entropyBits;
    }

    public double getCrackTimeSeconds() {
        return crackTimeSeconds;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public List<String> getMatchedPatterns() {
        return matchedPatterns;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    // --- builder -----------------------------------------------------------

    /**
     * A small builder so the analyser can assemble a report field by field
     * without leaking mutable state, and without a huge constructor signature.
     */
    public static class Builder {
        private String password;
        private int length;
        private boolean hasLower;
        private boolean hasUpper;
        private boolean hasDigit;
        private boolean hasSymbol;
        private int charClassCount;
        private double entropyBits;
        private double crackTimeSeconds;
        private PasswordStrength strength;
        private final List<String> matchedPatterns = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder length(int length) {
            this.length = length;
            return this;
        }

        public Builder hasLower(boolean v) { this.hasLower = v; return this; }
        public Builder hasUpper(boolean v) { this.hasUpper = v; return this; }
        public Builder hasDigit(boolean v) { this.hasDigit = v; return this; }
        public Builder hasSymbol(boolean v) { this.hasSymbol = v; return this; }

        public Builder charClassCount(int c) {
            this.charClassCount = c;
            return this;
        }

        public Builder entropyBits(double e) {
            this.entropyBits = e;
            return this;
        }

        public Builder crackTimeSeconds(double c) {
            this.crackTimeSeconds = c;
            this.strength = PasswordStrength.fromSeconds(c);
            return this;
        }

        public Builder addPattern(String p) {
            if (p != null && !p.isEmpty()) {
                this.matchedPatterns.add(p);
            }
            return this;
        }

        public Builder addWarning(String w) {
            if (w != null && !w.isEmpty()) {
                this.warnings.add(w);
            }
            return this;
        }

        public PasswordReport build() {
            return new PasswordReport(this);
        }
    }
}
