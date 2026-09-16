package com.passauditor.analyzer;

import com.passauditor.model.PasswordReport;
import com.passauditor.model.PasswordStrength;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style tests that exercise the full {@link PasswordAnalyzer}
 * pipeline end to end.
 */
class PasswordAnalyzerTest {

    private final PasswordAnalyzer analyzer =
            new PasswordAnalyzer(Set.of("password", "123456"), Set.of("admin"));

    @Test
    void weakPasswordGetsLowStrengthAndWarnings() {
        PasswordReport r = analyzer.analyze("password");
        assertEquals(8, r.getLength());
        assertTrue(r.getStrength() == PasswordStrength.VERY_WEAK
                || r.getStrength() == PasswordStrength.WEAK);
        assertFalse(r.getMatchedPatterns().isEmpty());
        assertFalse(r.getWarnings().isEmpty());
    }

    @Test
    void strongPasswordGetsHighStrengthAndNoPatterns() {
        PasswordReport r = analyzer.analyze("Tr0ub4dor&3xBp!");
        assertTrue(r.getEntropyBits() > 60);
        assertEquals(PasswordStrength.VERY_STRONG, r.getStrength());
        assertTrue(r.getMatchedPatterns().isEmpty());
    }

    @Test
    void nullPasswordIsHandledSafely() {
        PasswordReport r = analyzer.analyze(null);
        assertEquals(0, r.getLength());
        assertEquals(0.0, r.getEntropyBits());
        assertEquals(PasswordStrength.VERY_WEAK, r.getStrength());
    }

    @Test
    void shortPasswordTriggersLengthWarning() {
        PasswordReport r = analyzer.analyze("Ab1!");
        assertTrue(r.getWarnings().stream()
                .anyMatch(w -> w.startsWith("Shorter than")));
    }

    @Test
    void reportIsImmutable() {
        PasswordReport r = analyzer.analyze("Abcdef1!");
        assertThrows(UnsupportedOperationException.class, () -> r.getWarnings().add("x"));
    }
}
