package com.passauditor.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Detects weak patterns inside a password that dramatically reduce its real
 * entropy even when it looks long or complex.
 *
 * <p>The detector checks for:
 * <ul>
 *   <li>common leaked passwords ("password", "123456", "qwerty", ...),</li>
 *   <li>repeating characters ("aaaa", "1111"),</li>
 *   <li>sequential digits ("1234", "9876"),</li>
 *   <li>sequential letters ("abcd", "dcba"),</li>
 *   <li>keyboard walks along a standard QWERTY layout,</li>
 *   <li>years (1900-2099) and dictionary words.</li>
 * </ul>
 *
 * <p>Each match is recorded as a short human-readable label so the reporter can
 * surface actionable advice to the user.
 */
public final class PatternDetector {

    /** QWERTY rows, used for keyboard-walk detection. */
    private static final String[] KEYBOARD_ROWS = {
            "qwertyuiop",
            "asdfghjkl",
            "zxcvbnm"
    };

    // Common sequences scanned for inside the password (case-insensitive).
    private static final Pattern REPEAT = Pattern.compile("(.)\\1{2,}");
    private static final Pattern DIGIT_SEQ = Pattern.compile("(?:0123|1234|2345|3456|4567|5678|6789|7890|9876|8765|7654|6543|5432|4321|3210)");
    private static final Pattern ALPHA_SEQ = Pattern.compile("(?:abcd|bcde|cdef|defg|efgh|fghi|ghij|hijk|ijkl|jklm|klmn|lmno|mnop|nopq|opqr|pqrs|qrst|rstu|stuv|tuvw|uvwx|vwxy|wxyz|zyxw|yxwv|xwvu|wvut|vuts|utsw|tsrq|srqp|rqpo|qpon|ponm|onml|nmlk|mlkj|lkji|kjih|jihg|ihgf|hgfe|gfed|fedc|edcb|dcba|cba|dcba)");
    private static final Pattern YEAR = Pattern.compile("(?:19|20)\\d{2}");

    private final Set<String> commonPasswords;
    private final Set<String> dictionaryWords;

    /**
     * @param commonPasswords lowercase set of leaked/common passwords to flag
     * @param dictionaryWords  lowercase set of ordinary dictionary words to flag
     */
    public PatternDetector(Set<String> commonPasswords, Set<String> dictionaryWords) {
        this.commonPasswords = commonPasswords == null ? Set.of() : commonPasswords;
        this.dictionaryWords = dictionaryWords == null ? Set.of() : dictionaryWords;
    }

    /**
     * Runs every pattern check against the password and returns the labels of
     * those that matched. The password is not modified; matching is case
     * insensitive where it makes sense.
     */
    public List<String> detect(String password) {
        List<String> hits = new ArrayList<>();
        if (password == null || password.isEmpty()) {
            return hits;
        }

        String lower = password.toLowerCase();

        // 1. Exact or contained common passwords.
        if (commonPasswords.contains(lower)) {
            hits.add("Common leaked password");
        } else {
            for (String cp : commonPasswords) {
                if (cp.length() >= 4 && lower.contains(cp)) {
                    hits.add("Contains common password: " + cp);
                    break;
                }
            }
        }

        // 2. Repeating characters (3+ in a row).
        if (REPEAT.matcher(lower).find()) {
            hits.add("Repeating characters");
        }

        // 3. Digit sequences ("1234", "9876").
        if (DIGIT_SEQ.matcher(password).find()) {
            hits.add("Sequential digits");
        }

        // 4. Letter sequences ("abcd").
        if (ALPHA_SEQ.matcher(lower).find()) {
            hits.add("Sequential letters");
        }

        // 5. Keyboard walks.
        String walk = keyboardWalk(lower);
        if (walk != null) {
            hits.add("Keyboard walk: " + walk);
        }

        // 6. Years.
        if (YEAR.matcher(password).find()) {
            hits.add("Contains a year");
        }

        // 7. Dictionary words (only words of length >= 4 to avoid false positives).
        for (String word : dictionaryWords) {
            if (word.length() >= 4 && lower.contains(word)) {
                hits.add("Contains dictionary word: " + word);
                break;
            }
        }

        return hits;
    }

    /**
     * Looks for a run of 4+ consecutive keys along any QWERTY row. Returns the
     * matched substring, or null if no walk was found.
     */
    private String keyboardWalk(String lower) {
        for (String row : KEYBOARD_ROWS) {
            for (int i = 0; i <= row.length() - 4; i++) {
                String frag = row.substring(i, i + 4);
                if (lower.contains(frag)) {
                    return frag;
                }
                String reversed = new StringBuilder(frag).reverse().toString();
                if (lower.contains(reversed)) {
                    return reversed;
                }
            }
        }
        return null;
    }
}
