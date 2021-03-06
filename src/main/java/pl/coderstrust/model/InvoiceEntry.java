package pl.coderstrust.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.Builder;
import lombok.Data;

@JsonDeserialize(builder = InvoiceEntry.InvoiceEntryBuilder.class)
@ApiModel(value = "Invoice Entry", description = "Name, quantity and values of sold product")
@Data
@Builder(builderClassName = "InvoiceEntryBuilder", toBuilder = true)
public final class InvoiceEntry {

    @ApiModelProperty(value = "The unique identifier of the Invoice Entry", position = - 1, dataType = "Long")
    private final Long id;
    @ApiModelProperty(value = "Description of what is sold", example = "Siatka ogrodzeniowa")
    private final String description;
    @ApiModelProperty(value = "How much is sold", example = "5")
    private final Long quantity;
    @ApiModelProperty(value = "Value per unit", example = "123")
    private final BigDecimal price;
    @ApiModelProperty(value = "Total value before taxation", example = "500")
    private final BigDecimal netValue;
    @ApiModelProperty(value = "Total value after taxation", example = "615")
    private final BigDecimal grossValue;
    @ApiModelProperty(value = "Percentage level of taxation", example = "VAT_23")
    private final Vat vatRate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class InvoiceEntryBuilder {
    }
}
