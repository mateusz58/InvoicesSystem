package pl.coderstrust.service;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@EnableAutoConfiguration
@SpringBootTest(classes = {InvoiceEmailService.class})
@TestPropertySource(locations = "classpath:application-email.properties")
@ExtendWith(SpringExtension.class)
public class InvoiceEmailServiceTest {

    private GreenMail server;

    @Autowired
    private InvoiceEmailService emailSender;

    @Autowired
    private MailProperties mailProperties;

    @MockBean
    private InvoicePdfService invoicePdfService;

    @BeforeEach
    void setup() {
        ServerSetup setup = new ServerSetup(mailProperties.getPort(), mailProperties.getHost(), mailProperties.getProtocol());
        server = new GreenMail(setup);
        server.setUser(mailProperties.getUsername(), mailProperties.getUsername(), mailProperties.getPassword());
        server.start();
    }

    @AfterEach
    void finish() {
        server.stop();
    }

    @Test
    void sendMailWithInvoiceMethodShouldThrowExceptionForNullInvoiceArgument() {
        assertThrows(IllegalArgumentException.class, () -> emailSender.sendMailWithInvoice(null));
    }

    @Test
    void shouldSendEmail() throws MessagingException, IOException, ServiceOperationException {
        // When
        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
        doReturn("blablabla".getBytes()).when(invoicePdfService).createPdf(any());
        emailSender.sendMailWithInvoice(invoice);

        // Then
        await().atMost(Duration.ofSeconds(5))
            .untilAsserted(
                () -> assertEquals(1, server.getReceivedMessages().length));

        MimeMessage receivedMessage = server.getReceivedMessages()[0];
        assertEquals(mailProperties.getProperties().get("to"), receivedMessage.getAllRecipients()[0].toString());
        assertEquals(mailProperties.getProperties().get("title"), receivedMessage.getSubject());

        assertTrue(receivedMessage.getContentType().startsWith("multipart/mixed"));
        MimeMultipart body = (MimeMultipart) receivedMessage.getContent();
        assertTrue(body.getContentType().startsWith("multipart/mixed"));
        assertEquals(2, body.getCount());

        String textPart = (String)((MimeMultipart)body.getBodyPart(0).getContent()).getBodyPart(0).getContent();
        assertEquals(mailProperties.getProperties().get("content"), textPart);

        BodyPart attachmentPart = body.getBodyPart(1);
        assertTrue(attachmentPart.getContentType().equalsIgnoreCase(String.format("application/pdf; name=%s.pdf", invoice.getNumber())));
        InputStream attachmentStream = (InputStream) attachmentPart.getContent();
        byte[] pdf = IOUtils.toByteArray(attachmentStream);
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
