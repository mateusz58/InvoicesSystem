package pl.coderstrust.database.hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "CompanyBuilder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public  class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    
    private String name;
    
    private String address;
    
    private String taxId;
    
    private String accountNumber;
    
    private String phoneNumber;
    
    private String email;

    public static class CompanyBuilder {
    }

}
