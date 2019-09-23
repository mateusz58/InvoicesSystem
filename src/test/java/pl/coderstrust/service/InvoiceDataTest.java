package pl.coderstrust.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderstrust.generators.InvoiceGenerator;

class InvoiceDataTest {

    InvoiceData data;

    @BeforeEach
    void setup()
    {
        data=new InvoiceData();
    }

    @Test
    void shouldCreateBasicProfileData() {
        data.createBasicProfileData(InvoiceGenerator.generateRandomInvoice());
    }
}