package com.passauditor.entropy;

import com.passauditor.config.Config;

/**
 * Converts an entropy estimate into a wall-clock crack time.
 *
 * <p>Using the standard search-space model, the number of guesses needed is
 * 2^entropy, and at {@link Config#GUESSES_PER_SECOND} guesses per second the
 * average time to find the password is half that space:
 *
 * <pre>
 *   crackTime = 2^(entropy - 1) / guessesPerSecond   (seconds)
 * </pre>
 */
public final class CrackTimeEstimator {

    private final long guessesPerSecond;

    public CrackTimeEstimator() {
        this(Config.GUESSES_PER_SECOND);
    }

    /** Allows tests to inject a custom attack rate. */
    public CrackTimeEstimator(long guessesPerSecond) {
        if (guessesPerSecond <= 0) {
            throw new IllegalArgumentException("guessesPerSecond must be positive");
        }
        this.guessesPerSecond = guessesPerSecond;
    }

    /**
     * Returns the estimated average crack time in seconds.
     */
    public double seconds(double entropyBits) {
        if (entropyBits <= 0) {
            return 0.0;
        }
        // Average case is half the keyspace, hence (entropy - 1).
        double guesses = Math.pow(2, entropyBits - 1);
        return guesses / guessesPerSecond;
    }

    /**
     * Formats a crack time into a compact human-readable string, scaling up
     * through seconds, minutes, hours, days, years and beyond.
     */
    public String humanReadable(double seconds) {
        if (seconds < 1) {
            return "less than a second";
        }
        if (seconds < 60) {
            return String.format("%.0f seconds", seconds);
        }
        if (seconds < 3600) {
            return String.format("%.1f minutes", seconds / 60);
        }
        if (seconds < 86400) {
            return String.format("%.1f hours", seconds / 3600);
        }
        if (seconds < 86400 * 365) {
            return String.format("%.1f days", seconds / 86400);
        }
        double years = seconds / (86400.0 * 365);
        if (years < 1000) {
            return String.format("%.1f years", years);
        }
        if (years < 1_000_000) {
            return String.format("%.0f thousand years", years / 1000);
        }
        if (years < 1_000_000_000) {
            return String.format("%.0f million years", years / 1_000_000);
        }
        return String.format("%.3e years", years);
    }
}
