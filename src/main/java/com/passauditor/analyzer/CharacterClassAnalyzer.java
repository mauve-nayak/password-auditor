package com.passauditor.analyzer;

/**
 * Responsible for identifying which character classes (lowercase, uppercase,
 * digits, symbols) a password uses, and the size of the resulting alphabet.
 *
 * <p>The alphabet size is what entropy is built on: the more distinct classes a
 * password draws from, the larger the set of symbols an attacker must search.
 */
public final class CharacterClassAnalyzer {

    private CharacterClassAnalyzer() {
    }

    /** True if the password contains at least one lowercase letter a-z. */
    public static boolean hasLower(String password) {
        return password != null && password.chars().anyMatch(c -> c >= 'a' && c <= 'z');
    }

    /** True if the password contains at least one uppercase letter A-Z. */
    public static boolean hasUpper(String password) {
        return password != null && password.chars().anyMatch(c -> c >= 'A' && c <= 'Z');
    }

    /** True if the password contains at least one digit 0-9. */
    public static boolean hasDigit(String password) {
        return password != null && password.chars().anyMatch(c -> c >= '0' && c <= '9');
    }

    /**
     * True if the password contains at least one printable symbol that is not
     * a letter or digit (for example !@#$%^&*).
     */
    public static boolean hasSymbol(String password) {
        return password != null && password.chars()
                .anyMatch(c -> (c >= 33 && c <= 47)
                        || (c >= 58 && c <= 64)
                        || (c >= 91 && c <= 96)
                        || (c >= 123 && c <= 126));
    }

    /** Number of distinct character classes present. */
    public static int classCount(String password) {
        int count = 0;
        if (hasLower(password)) count++;
        if (hasUpper(password)) count++;
        if (hasDigit(password)) count++;
        if (hasSymbol(password)) count++;
        return count;
    }

    /**
     * Returns the effective alphabet size for the classes present in the
     * password (26 + 26 + 10 + 33).
     */
    public static int alphabetSize(String password) {
        int size = 0;
        if (hasLower(password)) size += 26;
        if (hasUpper(password)) size += 26;
        if (hasDigit(password)) size += 10;
        if (hasSymbol(password)) size += 33;
        return size;
    }
}
