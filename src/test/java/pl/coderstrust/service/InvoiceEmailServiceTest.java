package pl.coderstrust.service;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import java.time.Duration;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.coderstrust.generators.InvoiceGenerator;

@EnableAutoConfiguration
@SpringBootTest(classes = {InvoiceEmailService.class})
@TestPropertySource(locations = "classpath:application-email.properties")
@ExtendWith(SpringExtension.class)
public class InvoiceEmailServiceTest {

    private  GreenMail server;

    @Autowired
    private InvoiceEmailService emailSender;

    @Autowired
    private MailProperties mailProperties;

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
    void shouldSendEmail() throws MessagingException {
        // When
        emailSender.sendMailWithInvoice(InvoiceGenerator.generateRandomInvoice());

        // Then
        await().atMost(Duration.ofSeconds(5))
            .untilAsserted(
                () -> assertEquals(1, server.getReceivedMessages().length));

        MimeMessage receivedMessage = server.getReceivedMessages()[0];
        assertEquals(mailProperties.getProperties().get("to"), receivedMessage.getAllRecipients()[0].toString());
        assertEquals(mailProperties.getProperties().get("title"), receivedMessage.getSubject());
        assertEquals(mailProperties.getProperties().get("content"), GreenMailUtil.getBody(receivedMessage));
    }
}
