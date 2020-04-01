package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@JsonDeserialize(builder = Invoice.InvoiceBuilder.class)
@ApiModel(value = "Invoice", description = "Vat Invoice")
@Data
@Builder(builderClassName = "InvoiceBuilder", toBuilder = true)
public final class Invoice{

    @ApiModelProperty(value = "The unique identifier of the invoice", position = - 1, dataType = "Long")
    private  Long id;
    @ApiModelProperty(value = "Invoice number", example = "FV/1/05/2019")
    private  String number;
    @ApiModelProperty(value = "Date of Invoice creation", example = "2019-11-21")
    private  LocalDate issuedDate;
    @ApiModelProperty(value = "Term of payment", example = "2019-11-21")
    private  LocalDate dueDate;
    @ApiModelProperty(value = "Data of vendor")
    private  Company seller;
    @ApiModelProperty(value = "Data of customer")
    private  Company buyer;
    @ApiModelProperty(value = "Merchandise")
    private  List<InvoiceEntry> entries;

    @JsonPOJOBuilder(withPrefix = "")
    public static class InvoiceBuilder{
    }
}
