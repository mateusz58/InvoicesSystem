package pl.coderstrust.helpers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileHelper {

    void create(String filePath) throws IOException {
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
        if (!Files.exists(Paths.get(filePath))) {
            throw new FileNotFoundException("File does not exist.");
        }
        return (new File(filePath).length() == 0);
        if(br.readLine()==null)
        {
            br.close();
            return true;
        }
        br.close();
        return false;
    }

    void clear(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.flush();
        writer.close();
    }

    void writeLine(String filePath, String line) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8));
        writer.write(line);
        writer.close();
    }

    List<String> readLines(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8));
        List<String> result = reader.lines().collect(Collectors.toList());
        reader.close();
        return result;
    }

    String readLastLine(String filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8));
        String lastLine = reader.lines().reduce((first, second) -> second).orElse(null);
        reader.close();
        return lastLine;
    }

    void removeLine(String filePath, int lineNumber) throws IOException {
        String tempFile = "src/test/resources/helpers/temp.txt";
        if (filePath == null) {
            throw new IllegalArgumentException("Path of the file cannot be null");
        }
        if (lineNumber < 1) {
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
