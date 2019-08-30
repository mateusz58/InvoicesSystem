package pl.coderstrust.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    void createFile(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        Files.createFile(Paths.get(filePath));
    }


    void delete(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        Files.delete(Paths.get(filePath));
    }

    boolean exists(String filePath) {

        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        return Files.exists(Paths.get(filePath));
    }

    boolean isEmpty(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        return br.readLine() == null;
    }

    void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }

        Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.TRUNCATE_EXISTING);
    }

    void writeLine(String filePath, String Line) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
        writer.write(Line);
        writer.close();
    }

    List<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8));

        return reader.lines().collect(Collectors.toList());
    }

    String readLastLine(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8));
        return reader.lines().reduce((first, second) -> second).orElse(null);

    }

    void removeLine(String filePath, int lineNumber) throws IOException {

        String tempFile = "src/test/resources/helpers/temp.txt";

        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        if (lineNumber < 0) {
            throw new IllegalArgumentException("Line number cannot be lower than 0");
        }
        BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(tempFile));


        String currentLine;
        int i = 1;
        while ((currentLine = reader.readLine()) != null) {

            if (i == lineNumber) {
                i++;
                continue;
            }
            writer.write(currentLine + System.getProperty("line.separator"));
            i++;
        }
        writer.close();
        reader.close();
        Files.delete(Paths.get(filePath));
        Files.move(Paths.get(tempFile), Paths.get(filePath));

    }

}
