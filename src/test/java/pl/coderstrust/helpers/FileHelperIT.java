package pl.coderstrust.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FileHelperIT {

    private static final String INPUT_FILE = "src/test/resources/helpers/input_file.txt";
    private static final String EXPECTED_FILE = "src/test/resources/helpers/expected_file.txt";
    private static final String ENCODING = "UTF-8";
    private FileHelper fileHelper;
    private File inputFile;
    private File expectedFile;

    @BeforeEach
    void beforeEach() {
        fileHelper = new FileHelper();
        inputFile = new File(INPUT_FILE);
        if (inputFile.exists()) {
            inputFile.delete();
        }
        expectedFile = new File(EXPECTED_FILE);
        if (expectedFile.exists()) {
            expectedFile.delete();
        }
    }

    @Test
    void shouldCreateFile() throws IOException {
        fileHelper.createFile(INPUT_FILE);
        assertTrue(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldDeleteExistingFile() throws IOException {
        inputFile.createNewFile();
        fileHelper.delete(INPUT_FILE);
        assertFalse(Files.exists(Paths.get(INPUT_FILE)));
    }

    @Test
    void shouldReturnTrueIfFileExists() throws IOException {
        Files.createFile(Paths.get(INPUT_FILE));
        assertTrue(fileHelper.exists(INPUT_FILE));
    }

    @Test
    void shouldReturnFalseIfFileDoesNotExists() throws IOException {
        assertFalse(fileHelper.exists(INPUT_FILE));
    }

    @Test
    void shouldReturnFalseIfFileIsNotEmpty() throws IOException {
        FileUtils.writeLines(inputFile, Collections.singleton("bla bla bla"), ENCODING, true);
        assertFalse(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldReturnTrueIfFileIsEmpty() throws IOException {
        inputFile.createNewFile();
        assertTrue(fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldClearFile() throws IOException {
        expectedFile.createNewFile();
        FileUtils.writeLines(inputFile, Collections.singleton("bla bla bla"), ENCODING, true);
        fileHelper.clear(INPUT_FILE);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldWriteLineToFile() throws IOException {
        FileUtils.writeLines(expectedFile, ENCODING, Collections.singleton("test test"), true);
        fileHelper.writeLine(INPUT_FILE, "test test");
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void shouldReadLinesFromFile() throws IOException {
        List<String> expected = Arrays.asList(
            "Id",
            "22/2019",
            "2019-06-25",
            "2015-07-25",
            "Seller",
            "Buyer"
        );
        FileUtils.writeLines(inputFile, ENCODING, expected, true);
        List<String> actual = fileHelper.readLines(INPUT_FILE);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReadLastLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("Seller's details", "2019-06-25", "Buyer's details", "2019-06-25"), false);
        String result = fileHelper.readLastLine(INPUT_FILE);
        assertEquals("2019-06-25", result);
    }

    @Test
    void shouldRemoveLineFromFile() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("bla1", "bla2", "bla3"), true);
        FileUtils.writeLines(expectedFile, ENCODING, Arrays.asList("bla1", "bla3"), true);
        fileHelper.removeLine(INPUT_FILE, 2);
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void createMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.createFile(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.delete(null));
    }

    @Test
    void existsMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.exists(null));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.isEmpty(null));
    }

    @Test
    void clearMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.clear(null));
    }

    @Test
    void writeLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(null, "test"));
    }

    @Test
    void writeLineMethodShouldThrowExceptionForNullLineArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(INPUT_FILE, null));
    }

    @Test
    void readLinesMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.readLines(null));
    }

    @Test
    void readLastLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.readLastLine(null));
    }

    @Test
    void removeLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(null, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, - 1, - 3, - 5, - 3, - 20, Integer.MIN_VALUE})
    void removeLineMethodShouldThrowExceptionForInvalidLineNumberArgument(int lineNumber) {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(INPUT_FILE, lineNumber));
    }

    @Test
    void createMethodShouldThrowExceptionForExistingFile() throws IOException {
        inputFile.createNewFile();
        assertThrows(FileAlreadyExistsException.class, () -> fileHelper.createFile(INPUT_FILE));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(NoSuchFileException.class, () -> fileHelper.delete(INPUT_FILE));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(FileNotFoundException.class, () -> fileHelper.isEmpty(INPUT_FILE));
    }

    @Test
    void shouldReplaceLine() throws IOException {
        FileUtils.writeLines(inputFile, ENCODING, Arrays.asList("Line1", "LineToRemove", "Line3"), true);
        FileUtils.writeLines(expectedFile, ENCODING, Arrays.asList("Line1", "Line Added", "Line3"), true);
        fileHelper.replaceLine(INPUT_FILE, 2, "Line Added");
        assertTrue(FileUtils.contentEquals(expectedFile, inputFile));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(FileNotFoundException.class, () -> fileHelper.replaceLine(INPUT_FILE, 1, "Test"));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForNullFilePathArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(null, 1, "Test"));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForInvalidLineNumberArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(INPUT_FILE, - 1, "Test"));
    }

    @Test
    void replaceLineMethodShouldThrowExceptionForNullLineArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.replaceLine(INPUT_FILE, 1, null));
    }
}
