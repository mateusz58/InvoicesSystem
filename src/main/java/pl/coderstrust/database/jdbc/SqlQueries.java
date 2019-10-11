package pl.coderstrust.database.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import pl.coderstrust.database.DatabaseOperationException;

@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "jdbc")
public class SqlQueries {

    private static final String ENCODING = "UTF-8";
    public static String GET_INVOICE_BY_ID;
    public static String GET_INVOICE_BY_NUMBER;
    public static String GET_ALL_INVOICES;
    public static String DELETE_ALL_DATA;
    public static String GET_INVOICE_ENTRIES;
    public static String DELETE_INVOICE_BY_ID;
    public static String EXISTS_COMPANY;
    public static String EXISTS_INVOICE_ENTRY;
    public static String EXISTS_INVOICE;
    public static String COUNT_INVOICES;
    public static String UPDATE_INVOICE;
    public static String UPDATE_INVOICE_ENTRY;
    public static String INSERT_INVOICE;
    public static String INSERT_INVOICE_ENTRY;
    public static String INSERT_INVOICE_ENTRIES;
    public static String INSERT_COMPANY;

    static {
        try {
            GET_INVOICE_BY_ID = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET-INVOICE-BY-ID.sql"), ENCODING);
            GET_INVOICE_BY_NUMBER = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET-INVOICE-BY-NUM.sql"), ENCODING);
            GET_ALL_INVOICES = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET-ALL-INVOICES.sql"), ENCODING);
            DELETE_ALL_DATA = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DELETE-ALL-DATA.sql"), ENCODING);
            GET_INVOICE_ENTRIES = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET-INVOICE-ENTRIES.sql"), ENCODING);
            DELETE_INVOICE_BY_ID = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DELETE-INVOICE-BY-ID.sql"), ENCODING);
            EXISTS_COMPANY = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/EXISTS-COMPANY.sql"), ENCODING);
            EXISTS_INVOICE_ENTRY = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/EXISTS-INVOICE-ENTRY.sql"), ENCODING);
            EXISTS_INVOICE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/EXISTS-INVOICE.sql"), ENCODING);
            COUNT_INVOICES = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/COUNT-INVOICES.sql"), ENCODING);
            UPDATE_INVOICE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/UPDATE-INVOICE.sql"), ENCODING);
            UPDATE_INVOICE_ENTRY = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/UPDATE-INVOICE-ENTRY.sql"), ENCODING);
            INSERT_INVOICE = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/INSERT-INVOICE.sql"), ENCODING);
            INSERT_INVOICE_ENTRY = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/INSERT-INVOICE-ENTRY.sql"), ENCODING);
            INSERT_INVOICE_ENTRIES = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/INSERT-INVOICE-ENTRIES.sql"), ENCODING);
            INSERT_COMPANY = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/INSERT-COMPANY.sql"), ENCODING);
        } catch (IOException e) {
            try {
                throw new DatabaseOperationException(String.format("An error occured during getting scripts from file: %s", e));
            } catch (DatabaseOperationException ex) {
                System.out.println("Place for logger");
            }
        }
    }
}
