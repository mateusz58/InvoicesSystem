package pl.coderstrust.database.mongo;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceConstructor;

@Data
@Builder(builderClassName = "CompanyBuilder", toBuilder = true)
@NoArgsConstructor
public  class Company{

    private String name;
    private String address;
    private String taxId;
    private String accountNumber;
    private String phoneNumber;
    private String email;

    @PersistenceConstructor
    private Company(String name, String address, String taxId, String accountNumber, String phoneNumber, String email) {
        this.name = name;
        this.address = address;
        this.taxId = taxId;
        this.accountNumber = accountNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public static class CompanyBuilder {
    }
}