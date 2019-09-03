package pl.coderstrust.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileHelperTest {

    private static final String filePathInput = "src/test/resources/helpers/input_file.txt";
    FileHelper fileHelper;
    private File inputFile;

    @BeforeEach
    void beforeEach() {
        fileHelper = new FileHelper();
        inputFile = new File(filePathInput);
        if (inputFile.exists()) {
            inputFile.delete();
        }
    }

    @Test
    void shouldCreateFile() throws IOException {
        fileHelper.createFile(filePathInput);

        assertTrue(Files.exists(Paths.get(filePathInput)));
    }

    @Test
    void shouldDeleteFile() throws IOException {
        inputFile.createNewFile();
        fileHelper.delete(filePathInput);

        assertFalse(Files.exists(Paths.get(filePathInput)));
    }

    @Test
    void shouldReturnTrueIfFileExists() throws IOException {
        Files.createFile(Paths.get(filePathInput));

        assertTrue(fileHelper.exists(filePathInput));
    }

    @Test
    void shouldReturnFalseIfFileDoesNotExists() throws IOException {
        Files.createFile(Paths.get(filePathInput));
        Files.delete(Paths.get(filePathInput));

        assertFalse(fileHelper.exists(filePathInput));
    }

    @Test
    void shouldCheckIfFileIsEmpty() throws IOException {
        Files.createFile(Paths.get(filePathInput));
        assertTrue(fileHelper.isEmpty(filePathInput));
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        writer.write("Test data");
        writer.close();

        assertFalse(fileHelper.isEmpty(filePathInput));
    }

    @Test
    void shouldCheckIfFileWasCleared() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        writer.write("Test data");
        writer.close();
        fileHelper.clear(filePathInput);
        BufferedReader reader = new BufferedReader(new FileReader(filePathInput));
        String temp = reader.readLine();
        reader.close();

        assertTrue(temp == null);
    }

    @Test
    void shouldWriteGivenLineInFile() throws IOException {
        fileHelper.writeLine(filePathInput, "Test data");
        BufferedReader reader = new BufferedReader(new FileReader(filePathInput));
        String expected = reader.lines().reduce((first, second) -> second).orElse(null);
        reader.close();

        assertEquals(expected, "Test data");
    }

    @Test
    void shouldReadLines() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        List<String> expected = Arrays.asList(
            "Id",
            "22/2019",
            "2019-06-25",
            "2015-07-25",
            "Seller",
            "Buyer"
        );
        for (int i = 0; i < expected.size(); i++) {
            writer.write(expected.get(i) + System.getProperty("line.separator"));
        }
        writer.close();
        List<String> actual = fileHelper.readLines(filePathInput);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReadLastLineFromFile() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePathInput));
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePathInput));
        List<String> lines = Arrays.asList(
            "Id",
            "22/2019",
            "2019-06-25",
            "2015-07-25",
            "Seller",
            "Buyer"
        );
        for (int i = 0; i < lines.size(); i++) {
            writer.write(lines.get(i) + System.getProperty("line.separator"));
        }
        writer.close();
        String lastline = reader.lines().reduce((first, second) -> second).orElse(null);
        reader.close();

        assertEquals(lastline, fileHelper.readLastLine(filePathInput));
    }

    @Test
    void shouldRemoveLine() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        List<String> input = Arrays.asList(
            "Id",
            "22/2019",
            "2019-06-25",
            "2015-07-25",
            "Seller",
            "Buyer"
        );
        List<String> expected = Arrays.asList(
            "Id",
            "22/2019",
            "2015-07-25",
            "Seller",
            "Buyer"
        );
        for (int i = 0; i < input.size(); i++) {
            writer.write(input.get(i) + System.getProperty("line.separator"));
        }
        writer.close();
        fileHelper.removeLine(filePathInput, 3);
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePathInput));
        List<String> actual = reader.lines().collect(Collectors.toList());
        reader.close();

        assertEquals(expected, actual);
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
        assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLine(filePathInput, null));
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

    @Test
    void removeLineMethodShouldThrowExceptionForLineNumberSmallerThanOneArgument() {
        assertThrows(IllegalArgumentException.class, () -> fileHelper.removeLine(filePathInput, 0));
    }

    @Test
    void createMethodShouldThrowExceptionForExistingFile() throws IOException {
        inputFile.createNewFile();

        assertThrows(FileAlreadyExistsException.class, () -> fileHelper.createFile(filePathInput));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(NoSuchFileException.class, () -> fileHelper.delete(filePathInput));
    }

    @Test
    void isEmptyMethodShouldThrowExceptionForNonExistingFile() {
        assertThrows(FileNotFoundException.class, () -> fileHelper.isEmpty(filePathInput));
    }
}
