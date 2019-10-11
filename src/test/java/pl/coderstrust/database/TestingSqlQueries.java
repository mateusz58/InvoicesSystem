package pl.coderstrust.database;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class TestingSqlQueries {

    private static final String ENCODING = "UTF-8";
    public static String CREATE_TABLE;
    public static String DROP_TABLE;
    public static String TRIGGERS;
    public static String DROP_TRIGGERS;
    public static String EXISTS_DATA;

    static {
        try {
            CREATE_TABLE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/CREATE-TABLES.sql"), ENCODING);
            DROP_TABLE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DROP-ALL-TABLES.sql"), ENCODING);
            TRIGGERS = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/TRIGGERS.sql"), ENCODING);
            DROP_TRIGGERS = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DROP-TRIGGERS.sql"), ENCODING);
            EXISTS_DATA = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/EXISTS-DATA.sql"), ENCODING);
        } catch (IOException e) {
            System.out.println("Testing");
        }
    }
}
