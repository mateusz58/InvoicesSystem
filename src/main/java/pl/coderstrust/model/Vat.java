package pl.coderstrust.model;

import java.math.BigDecimal;

public enum Vat {

    VAT_0(new BigDecimal("0.00")),
    VAT_5(new BigDecimal("0.05")),
    VAT_8(new BigDecimal("0.08")),
    VAT_23(new BigDecimal("0.23"));

    private final BigDecimal value;

    Vat(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Vat{"
                + "value=" + value
                + '}';
    }
}
