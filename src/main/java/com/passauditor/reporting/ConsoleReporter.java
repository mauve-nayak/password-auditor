package com.passauditor.reporting;

import com.passauditor.entropy.CrackTimeEstimator;
import com.passauditor.model.PasswordReport;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Writes the audit results to the terminal in a readable, aligned table.
 *
 * <p>This is the default reporter used by the CLI. It prints a per-password
 * breakdown followed by a short summary, and it deliberately masks the actual
 * password characters so the screen output is safe to screenshot or share.
 */
public class ConsoleReporter {

    private static final CrackTimeEstimator TIME_FMT = new CrackTimeEstimator();

    private final PrintStream out;

    public ConsoleReporter() {
        this(System.out);
    }

    public ConsoleReporter(PrintStream out) {
        this.out = out;
    }

    /** Prints the full audit for a list of reports. */
    public void report(List<PasswordReport> reports) {
        out.println();
        out.println("================  PASSWORD STRENGTH AUDIT  ================");
        out.println();

        int idx = 1;
        for (PasswordReport r : reports) {
            out.printf("#%d  %s%n", idx++, mask(r.getPassword()));
            out.printf("     Length          : %d%n", r.getLength());
            out.printf("     Character classes: %s (%d)%n", classes(r), r.getCharClassCount());
            out.printf("     Entropy         : %.1f bits%n", r.getEntropyBits());
            out.printf("     Est. crack time  : %s%n", TIME_FMT.humanReadable(r.getCrackTimeSeconds()));
            out.printf("     Strength         : %s%n", r.getStrength().getLabel());

            if (!r.getMatchedPatterns().isEmpty()) {
                out.println("     Patterns found   :");
                for (String p : r.getMatchedPatterns()) {
                    out.println("       - " + p);
                }
            }
            if (!r.getWarnings().isEmpty()) {
                out.println("     Warnings         :");
                for (String w : r.getWarnings()) {
                    out.println("       ! " + w);
                }
            }
            out.println();
        }

        printSummary(reports);
    }

    private void printSummary(List<PasswordReport> reports) {
        int weak = 0, fair = 0, strong = 0;
        double avgEntropy = 0;
        for (PasswordReport r : reports) {
            avgEntropy += r.getEntropyBits();
            switch (r.getStrength()) {
                case VERY_WEAK:
                case WEAK:
                    weak++;
                    break;
                case FAIR:
                    fair++;
                    break;
                case STRONG:
                case VERY_STRONG:
                    strong++;
                    break;
            }
        }
        avgEntropy = reports.isEmpty() ? 0 : avgEntropy / reports.size();

        out.println("------------------------  SUMMARY  ------------------------");
        out.printf("Passwords audited : %d%n", reports.size());
        out.printf("Average entropy   : %.1f bits%n", avgEntropy);
        out.printf("Weak / Fair / Strong : %d / %d / %d%n", weak, fair, strong);
        out.println("===========================================================");
    }

    private String classes(PasswordReport r) {
        StringBuilder sb = new StringBuilder();
        if (r.hasLower()) sb.append("a-z ");
        if (r.hasUpper()) sb.append("A-Z ");
        if (r.hasDigit()) sb.append("0-9 ");
        if (r.hasSymbol()) sb.append("!@#");
        return sb.toString().trim();
    }

    /** Shows the first and last character, masking everything in between. */
    static String mask(String password) {
        if (password == null || password.isEmpty()) {
            return "(empty)";
        }
        if (password.length() <= 2) {
            return "*".repeat(password.length());
        }
        return password.charAt(0) + "*".repeat(password.length() - 2) + password.charAt(password.length() - 1);
    }

    /** Convenience overload for a single report. */
    public void report(PasswordReport r) {
        report(List.of(r));
    }
}
