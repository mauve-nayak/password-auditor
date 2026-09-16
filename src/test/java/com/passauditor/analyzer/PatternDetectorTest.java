package com.passauditor.analyzer;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the pattern detector, covering each kind of weak pattern it
 * can flag.
 */
class PatternDetectorTest {

    private final PatternDetector detector =
            new PatternDetector(Set.of("password", "123456", "qwerty"), Set.of("admin", "letmein"));

    @Test
    void flagsCommonPassword() {
        assertTrue(detector.detect("password").contains("Common leaked password"));
    }

    @Test
    void flagsContainedCommonPassword() {
        var hits = detector.detect("myPassword123!");
        assertTrue(hits.stream().anyMatch(h -> h.startsWith("Contains common password")));
    }

    @Test
    void flagsRepeatingCharacters() {
        assertTrue(detector.detect("aaaabc").contains("Repeating characters"));
    }

    @Test
    void flagsSequentialDigits() {
        assertTrue(detector.detect("abc1234").contains("Sequential digits"));
    }

    @Test
    void flagsKeyboardWalk() {
        var hits = detector.detect("qwerty123");
        assertTrue(hits.stream().anyMatch(h -> h.startsWith("Keyboard walk")));
    }

    @Test
    void flagsYear() {
        assertTrue(detector.detect("secret2024").contains("Contains a year"));
    }

    @Test
    void flagsDictionaryWord() {
        assertTrue(detector.detect("admin1234").stream()
                .anyMatch(h -> h.startsWith("Contains dictionary word")));
    }

    @Test
    void cleanPasswordTriggersNoPatterns() {
        assertTrue(detector.detect("9$Kp#vL2!zQ").isEmpty());
    }

    @Test
    void nullAndPasswordsReturnEmpty() {
        assertTrue(detector.detect(null).isEmpty());
        assertTrue(detector.detect("").isEmpty());
    }
}
