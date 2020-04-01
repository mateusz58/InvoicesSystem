package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@JsonDeserialize(builder = Company.CompanyBuilder.class)
@ApiModel(value = "Company", description = "Company")
@Data
@Builder(builderClassName = "CompanyBuilder", toBuilder = true)
public final class Company{

    @ApiModelProperty(value = "The unique identifier of the company.", position = - 1, dataType = "Long")
    private  Long id;
    @ApiModelProperty(value = "Company name.", example = "CodersTrust")
    private  String name;
    @ApiModelProperty(value = "Company address.", example = "ul. Bukowińska 24 d/7, 02-703 Warszawa")
    private  String address;
    @ApiModelProperty(value = "Company tax id.", example = "7010416384")
    private  String taxId;
    @ApiModelProperty(value = "Company bank account number.", example = "27 1030 0019 0109 8503 0014 2668")
    private  String accountNumber;
    @ApiModelProperty(value = "Company telephone number", example = "22 788-83-22")
    private  String phoneNumber;
    @ApiModelProperty(value = "Company email address", example = "example@post.com.pl")
    private  String email;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CompanyBuilder {
    }

}