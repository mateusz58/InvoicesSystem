package pl.coderstrust.configuration;

import org.springframework.stereotype.Component;

@Component
public class InFileDatabaseProperties {
    private   String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
