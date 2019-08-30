package pl.coderstrust.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    private static final String filePathInput = "src/test/resources/helpers/input_file.txt";
    FileHelper filehelper;
    private File inputFile;

    @BeforeEach
    void setUp() {
        filehelper = new FileHelper();
        if (Files.exists(Paths.get(filePathInput))) {
            inputFile.delete();
        }
    }

    @Test
    void shouldCreateFile() throws IOException {

        filehelper.createFile(filePathInput);

        assertTrue(Files.exists(Paths.get(filePathInput)));
    }

    @Test
    void shouldDeleteFile() throws IOException {

        Files.createFile(Paths.get(filePathInput));

        filehelper.delete(filePathInput);

        assertFalse(Files.exists(Paths.get(filePathInput)));

    }

    @Test
    void shouldCheckIfFileExists() throws IOException {

        Files.createFile(Paths.get(filePathInput));

        assertTrue(filehelper.exists(filePathInput));

        Files.delete(Paths.get(filePathInput));

        assertFalse(filehelper.exists(filePathInput));
    }

    @Test
    void shouldCheckIfFileIsEmpty() throws IOException {

        Files.createFile(Paths.get(filePathInput));

        assertTrue(filehelper.isEmpty(filePathInput));

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        writer.write("Test data");
        writer.close();

        assertFalse(filehelper.isEmpty(filePathInput));

    }

    @Test
    void shouldCheckIfFileWasCleared() throws IOException {


        BufferedWriter writer = new BufferedWriter(new FileWriter(filePathInput));
        writer.write("Test data");
        writer.close();

        filehelper.clear(filePathInput);

        BufferedReader reader = new BufferedReader(new FileReader(filePathInput));

        assertTrue(reader.readLine() == null);
    }

    @Test
    void shouldWriteGivenLineInFile() throws IOException {

        String givenLine = "Test data";

        filehelper.writeLine(filePathInput, givenLine);
        BufferedReader reader = new BufferedReader(new FileReader(filePathInput));

        String expected = reader.lines().reduce((first, second) -> second).orElse(null);

        assertEquals(expected, givenLine);
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

        List<String> actual = filehelper.readLines(filePathInput);

        assertEquals(expected, actual);
    }

    @Test
    void shouldReadLastLine() throws IOException {

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

        assertEquals(lastline, filehelper.readLastLine(filePathInput));
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

        filehelper.removeLine(filePathInput, 3);

        BufferedReader reader = Files.newBufferedReader(Paths.get(filePathInput));

        List<String> actual = reader.lines().collect(Collectors.toList());

        assertEquals(expected, actual);

    }
}