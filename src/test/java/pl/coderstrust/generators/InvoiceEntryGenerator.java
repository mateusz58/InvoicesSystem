package pl.coderstrust.generators;

import static pl.coderstrust.generators.IdGenerator.getRandomId;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceEntryGenerator {

    private static Faker faker = new Faker(new Locale("pl"));

    public static InvoiceEntry getRandomEntry() {
        Long quantity = NumberGenerator.generateRandomNumber(1) + 1;
        BigDecimal price = BigDecimal.valueOf(NumberGenerator.generateRandomNumber(2) + 1);
        BigDecimal netValue = price.multiply(BigDecimal.valueOf(quantity));
        Vat vatRate = VatRateGenerator.getRandomVatRate(Vat.class);
        BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate.getValue())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grossValue = netValue.add(vatValue);
        return InvoiceEntry.builder()
            .withId(getRandomId())
            .withDescription(faker.commerce().productName())
            .withQuantity(quantity)
            .withPrice(price)
            .withNetValue(netValue)
            .withGrossValue(grossValue)
            .withVatRate(vatRate)
            .build();
    }
}
