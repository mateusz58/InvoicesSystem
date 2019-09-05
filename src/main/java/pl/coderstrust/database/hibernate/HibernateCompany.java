package pl.coderstrust.database.hibernate;

import java.util.Collection;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "company")
public class HibernateCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private final Long id;

    @Column(name = "name")
    private final String name;

    @Column(name = "address")
    private final String address;

    @Column(name = "tax_id")
    private final String taxId;

    @Column(name = "account_number")
    private final String accountNumber;

    @Column(name = "phone_number")
    private final String phoneNumber;

    @Column(name = "email")
    private final String email;

    @Column(name = "sales invoices")
    @OneToMany(mappedBy="company")
    private List<HibernateInvoice> salesInvoices;

    @Column(name = "purchase invoices")
    @OneToMany(mappedBy="company")
    private List<HibernateInvoice> purchaseInvoices;


    private HibernateCompany(HibernateCompany.Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
    }

    public static HibernateCompany.Builder builder() {
        return new HibernateCompany.Builder();
    }

    public static class Builder {

        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public HibernateCompany.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public HibernateCompany.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public HibernateCompany.Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public HibernateCompany.Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public HibernateCompany.Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public HibernateCompany.Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public HibernateCompany.Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public HibernateCompany build() {
            return new HibernateCompany(this);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HibernateCompany company = (HibernateCompany) o;
        return Objects.equals(id, company.id)
                && Objects.equals(name, company.name)
                && Objects.equals(address, company.address)
                && Objects.equals(taxId, company.taxId)
                && Objects.equals(accountNumber, company.accountNumber)
                && Objects.equals(phoneNumber, company.phoneNumber)
                && Objects.equals(email, company.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Company{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", address='" + address + '\''
                + ", taxId='" + taxId + '\''
                + ", accountNumber='" + accountNumber + '\''
                + ", phoneNumber='" + phoneNumber + '\''
                + ", email='" + email + '\''
                + '}';
    }
}
