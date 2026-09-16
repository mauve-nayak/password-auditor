package com.passauditor.entropy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for entropy calculation and crack-time estimation.
 */
class EntropyCalculatorTest {

    @Test
    void emptyPasswordHasZeroEntropy() {
        assertEquals(0.0, EntropyCalculator.calculate(""));
        assertEquals(0.0, EntropyCalculator.calculate(null));
    }

    @Test
    void singleClassPasswordHasExpectedEntropy() {
        // "abcd": 4 lowercase letters, alphabet=26 -> 4 * log2(26) ~ 18.8
        double e = EntropyCalculator.calculate("abcd");
        assertEquals(4 * (Math.log(26) / Math.log(2)), e, 0.001);
    }

    @Test
    void moreClassesMeansMoreEntropyForSameLength() {
        double lowOnly = EntropyCalculator.calculate("abcdefgh");
        double mixed = EntropyCalculator.calculate("AbcDef1!");
        assertTrue(mixed > lowOnly,
                "Mixed-class password should have higher entropy than same-length lowercase only");
    }
}

class CrackTimeEstimatorTest {

    private final CrackTimeEstimator estimator = new CrackTimeEstimator(1_000_000L);

    @Test
    void zeroEntropyMeansInstantCrack() {
        assertEquals(0.0, estimator.seconds(0));
    }

    @Test
    void higherEntropyMeansLongerCrackTime() {
        assertTrue(estimator.seconds(40) > estimator.seconds(20));
    }

    @Test
    void humanReadableScalesThroughUnits() {
        assertTrue(estimator.humanReadable(0.5).contains("second"));
        assertTrue(estimator.humanReadable(120).contains("minute"));
        assertTrue(estimator.humanReadable(7200).contains("hour"));
        assertTrue(estimator.humanReadable(86400 * 5).contains("day"));
        assertTrue(estimator.humanReadable(86400 * 365 * 10).contains("year"));
    }

    @Test
    void rejectsNonPositiveRate() {
        assertThrows(IllegalArgumentException.class, () -> new CrackTimeEstimator(0));
        assertThrows(IllegalArgumentException.class, () -> new CrackTimeEstimator(-5));
    }
}
