package com.passauditor.analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the character-class analysis.
 */
class CharacterClassAnalyzerTest {

    @Test
    void detectsLowercaseOnly() {
        assertTrue(CharacterClassAnalyzer.hasLower("abc"));
        assertFalse(CharacterClassAnalyzer.hasUpper("abc"));
        assertFalse(CharacterClassAnalyzer.hasDigit("abc"));
        assertFalse(CharacterClassAnalyzer.hasSymbol("abc"));
        assertEquals(1, CharacterClassAnalyzer.classCount("abc"));
        assertEquals(26, CharacterClassAnalyzer.alphabetSize("abc"));
    }

    @Test
    void detectsAllFourClasses() {
        String pw = "Abc!123";
        assertTrue(CharacterClassAnalyzer.hasLower(pw));
        assertTrue(CharacterClassAnalyzer.hasUpper(pw));
        assertTrue(CharacterClassAnalyzer.hasDigit(pw));
        assertTrue(CharacterClassAnalyzer.hasSymbol(pw));
        assertEquals(4, CharacterClassAnalyzer.classCount(pw));
        assertEquals(26 + 26 + 10 + 33, CharacterClassAnalyzer.alphabetSize(pw));
    }

    @Test
    void nullAndPasswordsAreSafe() {
        assertFalse(CharacterClassAnalyzer.hasLower(null));
        assertEquals(0, CharacterClassAnalyzer.classCount(null));
        assertEquals(0, CharacterClassAnalyzer.alphabetSize(""));
    }
}
