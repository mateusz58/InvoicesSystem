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
        return getRandomEntryWithSpecificId(getRandomId());
    }

    public static InvoiceEntry getRandomEntryWithSpecificId(Long id) {
        Long quantity = NumberGenerator.generateRandomNumber(1) + 1;
        BigDecimal price = BigDecimal.valueOf(NumberGenerator.generateRandomNumber(2) + 1).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal netValue = price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_EVEN);
        Vat vatRate = VatRateGenerator.getRandomVatRate(Vat.class);
        BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate.getValue())).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal grossValue = netValue.add(vatValue).setScale(2, RoundingMode.HALF_EVEN);
        return InvoiceEntry.builder()
            .id(id)
            .description(faker.commerce().productName())
            .quantity(quantity)
            .price(price)
            .netValue(netValue)
            .grossValue(grossValue)
            .vatRate(vatRate)
            .build();
    }
}
