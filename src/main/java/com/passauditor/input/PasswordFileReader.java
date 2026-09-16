package com.passauditor.input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads passwords from a local text file, one per line.
 *
 * <p>Blank lines and surrounding whitespace are stripped, and a maximum line
 * length is enforced to guard against accidentally loading a binary file. No
 * password ever leaves the process - this class only touches the local
 * filesystem.
 */
public final class PasswordFileReader {

    /** Rejects absurdly long lines that probably indicate a binary file. */
    private static final int MAX_LINE_LENGTH = 1024;

    /**
     * Reads and sanitises the password list.
     *
     * @param file path to a UTF-8 text file with one password per line
     * @return the cleaned list of passwords (never null)
     * @throws IOException        if the file cannot be read
     * @throws InvalidFileException if a line exceeds {@link #MAX_LINE_LENGTH}
     */
    public List<String> read(Path file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file path must not be null");
        }
        if (!Files.exists(file)) {
            throw new IOException("Input file not found: " + file);
        }

        List<String> raw = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<String> passwords = new ArrayList<>(raw.size());
        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.length() > MAX_LINE_LENGTH) {
                throw new InvalidFileException(
                        "Line " + (i + 1) + " exceeds the " + MAX_LINE_LENGTH
                                + " character limit - is this a text file?");
            }
            passwords.add(line);
        }
        return passwords;
    }

    /** Raised when the input file does not look like a list of passwords. */
    public static class InvalidFileException extends RuntimeException {
        public InvalidFileException(String message) {
            super(message);
        }
    }
}
