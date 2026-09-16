package com.passauditor.config;

/**
 * Central place for tunable constants. Keeping the magic numbers here (instead
 * of scattering them through the analyzers) makes the behaviour easy to reason
 * about and to adjust without touching analysis logic.
 */
public final class Config {

    private Config() {
        // Utility class - not meant to be instantiated.
    }

    /** Guesses per second an offline attacker is assumed to perform. */
    public static final long GUESSES_PER_SECOND = 10_000_000_000L; // 10 billion

    /** Minimum length a password should reach to be considered reasonably safe. */
    public static final int MIN_RECOMMENDED_LENGTH = 12;

    /** Default path to the list of common/leaked passwords to flag. */
    public static final String COMMON_PASSWORDS_FILE = "/data/common_passwords.txt";

    /** Default input file holding one password per line. */
    public static final String DEFAULT_INPUT_FILE = "data/sample_passwords.txt";
}
