package com.passauditor;

import com.passauditor.analyzer.PasswordAnalyzer;
import com.passauditor.input.PasswordFileReader;
import com.passauditor.model.PasswordReport;
import com.passauditor.reporting.ConsoleReporter;
import com.passauditor.reporting.HtmlReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Command-line entry point for the Personal Password Strength Auditor.
 *
 * <p>Usage:
 * <pre>
 *   java -jar password-auditor.jar <passwords-file> [--html report.html]
 * </pre>
 *
 * <p>The tool reads one password per line from the given file, analyses each
 * password entirely offline, and prints a per-password report plus a summary to
 * the console. Optionally it also writes an HTML report. No password is ever
 * transmitted anywhere - everything happens locally in this process.
 */
public final class Main {

    public static void main(String[] args) {
                // Inline password audit: java ... Main --pass "mypassword"
        if (args.length >= 2 && "--pass".equals(args[0])) {
            try {
                Set<String> common = loadWordList(Path.of("data/common_passwords.txt"));
                Set<String> dictionary = loadWordList(Path.of("data/dictionary_words.txt"));
                PasswordReport report =
                        new PasswordAnalyzer(common, dictionary).analyze(args[1]);
                new ConsoleReporter().report(report);
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                System.exit(2);
            }
            return;
        }

        if (args.length < 1) {
            System.err.println("Usage: java -jar password-auditor.jar <passwords-file> [--html report.html]");
            System.err.println("Example: java -jar password-auditor.jar data/sample_passwords.txt --html audit.html");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);
        Path htmlOutput = null;
        if (args.length >= 3 && "--html".equals(args[1])) {
            htmlOutput = Path.of(args[2]);
        }

        try {
            run(inputFile, htmlOutput);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        } catch (PasswordFileReader.InvalidFileException e) {
            System.err.println("Invalid input file: " + e.getMessage());
            System.exit(3);
        }
    }

    /**
     * Loads the supporting word lists, reads the passwords, runs the analysis
     * and emits the reports. Kept separate from {@link #main} so it can be
     * exercised directly from tests.
     */
    static void run(Path inputFile, Path htmlOutput) throws IOException {
        // Load the common-passwords and dictionary word lists from the data folder.
        Set<String> common = loadWordList(Path.of("data/common_passwords.txt"));
        Set<String> dictionary = loadWordList(Path.of("data/dictionary_words.txt"));

        List<String> passwords = new PasswordFileReader().read(inputFile);
        if (passwords.isEmpty()) {
            System.out.println("No passwords found in " + inputFile + ".");
            return;
        }

        PasswordAnalyzer analyzer = new PasswordAnalyzer(common, dictionary);
        List<PasswordReport> reports = passwords.stream()
                .map(analyzer::analyze)
                .toList();

        new ConsoleReporter().report(reports);

        if (htmlOutput != null) {
            new HtmlReporter().write(reports, htmlOutput);
            System.out.println("HTML report written to: " + htmlOutput);
        }
    }

    /** Loads a newline-separated word list into a lowercased set; tolerates missing files. */
    private static Set<String> loadWordList(Path file) throws IOException {
        Set<String> words = new HashSet<>();
        if (!Files.exists(file)) {
            return words;
        }
        for (String line : Files.readAllLines(file)) {
            String w = line.trim().toLowerCase();
            if (!w.isEmpty()) {
                words.add(w);
            }
        }
        return words;
    }
}
