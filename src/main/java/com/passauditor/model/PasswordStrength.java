package com.passauditor.model;

/**
 * Enum representing the qualitative strength bucket assigned to a password.
 *
 * <p>The bucket is derived from the estimated crack time, so it gives a quick,
 * human-readable verdict alongside the raw entropy and time figures.
 */
public enum PasswordStrength {
    VERY_WEAK("Very Weak"),
    WEAK("Weak"),
    FAIR("Fair"),
    STRONG("Strong"),
    VERY_STRONG("Very Strong");

    private final String label;

    PasswordStrength(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Maps a crack-time in seconds to a qualitative strength bucket. The
     * thresholds are intentionally conservative so that anything crackable
     * within an hour is treated as weak, while multi-year passwords are strong.
     *
     * @param seconds estimated time to crack, in seconds
     * @return a matching {@link PasswordStrength} bucket
     */
    public static PasswordStrength fromSeconds(double seconds) {
        if (seconds < 60) {
            return VERY_WEAK;          // less than a minute
        } else if (seconds < 3600) {
            return WEAK;                // less than an hour
        } else if (seconds < 86400 * 30) {
            return FAIR;                // less than a month
        } else if (seconds < 86400 * 365) {
            return STRONG;              // less than a year
        } else {
            return VERY_STRONG;         // a year or more
        }
    }
}
