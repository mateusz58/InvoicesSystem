package pl.coderstrust.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceEmailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;

    @Autowired
    public InvoiceEmailService(JavaMailSender javaMailSender, MailProperties mailProperties) {
        this.mailProperties = mailProperties;
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendMailWithInvoice(Invoice invoice) {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailProperties.getUsername());
        mail.setTo(mailProperties.getProperties().get("to"));
        mail.setSubject(mailProperties.getProperties().get("title"));
        mail.setText(mailProperties.getProperties().get("content"));
        javaMailSender.send(mail);
    }
}
