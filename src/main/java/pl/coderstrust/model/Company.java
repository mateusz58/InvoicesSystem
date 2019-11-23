package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@JsonDeserialize(builder = Company.Builder.class)
@ApiModel(value = "Company", description = "Company")
public final class Company {

    @ApiModelProperty(value = "The unique identifier of the company.", position = - 1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "Company name.", example = "CodersTrust")
    private final String name;
    @ApiModelProperty(value = "Company address.", example = "ul. Bukowińska 24 d/7, 02-703 Warszawa")
    private final String address;
    @ApiModelProperty(value = "Company tax id.", example = "7010416384")
    private final String taxId;
    @ApiModelProperty(value = "Company bank account number.", example = "27 1030 0019 0109 8503 0014 2668")
    private final String accountNumber;
    @ApiModelProperty(value = "Company telephone number", example = "22 788-83-22")
    private final String phoneNumber;
    @ApiModelProperty(value = "Company email address", example = "example@post.com.pl")
    private final String email;

    private Company(Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
    }

    public static Company.Builder builder() {
        return new Company.Builder();
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
    public int hashCode() {
        return Objects.hash(id, name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
        return Objects.equals(id, company.id)
            && Objects.equals(name, company.name)
            && Objects.equals(address, company.address)
            && Objects.equals(taxId, company.taxId)
            && Objects.equals(accountNumber, company.accountNumber)
            && Objects.equals(phoneNumber, company.phoneNumber)
            && Objects.equals(email, company.email);
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

    @JsonPOJOBuilder
    public static class Builder {

        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
