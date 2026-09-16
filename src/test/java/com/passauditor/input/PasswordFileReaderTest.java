package com.passauditor.input;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the file reader, covering blank-line stripping, missing files and
 * the binary-file guard.
 */
class PasswordFileReaderTest {

    private final PasswordFileReader reader = new PasswordFileReader();

    @Test
    void readsAndTrimsLines(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("pws.txt");
        Files.writeString(file, "  hello  \n\nworld\n\n");
        List<String> pws = reader.read(file);
        assertEquals(List.of("hello", "world"), pws);
    }

    @Test
    void missingFileThrowsIOException() {
        assertThrows(IOException.class, () -> reader.read(Path.of("does-not-exist-12345.txt")));
    }

    @Test
    void nullPathThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> reader.read(null));
    }

    @Test
    void overlongLineThrowsInvalidFileException(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("big.txt");
        Files.writeString(file, "a".repeat(2000));
        assertThrows(PasswordFileReader.InvalidFileException.class, () -> reader.read(file));
    }

    @Test
    void emptyFileYieldsEmptyList(@TempDir Path tmp) throws IOException {
        Path file = tmp.resolve("empty.txt");
        Files.createFile(file);
        assertTrue(reader.read(file).isEmpty());
    }
}
