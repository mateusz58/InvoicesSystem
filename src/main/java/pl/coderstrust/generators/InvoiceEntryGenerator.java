package pl.coderstrust.generators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceEntryGenerator {

    public static InvoiceEntry getRandomEntry() {
        Long quantity = Long.valueOf(Generator.generateRandomNumber(2));
        BigDecimal price = BigDecimal.valueOf(Generator.generateRandomNumber(3));
        BigDecimal netValue = price.multiply(BigDecimal.valueOf(quantity));
        Vat vatRate = VatRateGenerator.getRandomVatRate(Vat.class);
        BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate.getValue())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grossValue = netValue.add(vatValue);
        return InvoiceEntry.builder()
                .withId(IdGenerator.getId())
                .withDescription(Generator.generateRandomWord())
                .withQuantity(quantity)
                .withPrice(price)
                .withNetValue(netValue)
                .withGrossValue(grossValue)
                .withVatRate(vatRate)
                .build();
    }
}
