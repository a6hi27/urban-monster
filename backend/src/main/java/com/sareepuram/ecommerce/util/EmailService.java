package com.sareepuram.ecommerce.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


import org.springframework.stereotype.Service;

import java.nio.file.Paths;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendInvoiceEmail(String recipientMail, String invoiceNumber, String pdfPath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("orders@urbanmonster.com");
        helper.setTo(recipientMail);
        helper.setSubject("Your order has been received!");
        helper.setText("Please find attached your invoice.");
        FileSystemResource pdfFile = new FileSystemResource(pdfPath);
        helper.addAttachment(invoiceNumber + ".pdf", pdfFile);
        mailSender.send(message);
    }

    public void sendEmail(String recipientMail, String senderMail, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(senderMail);
        helper.setTo(recipientMail);
        helper.setSubject(subject);
        helper.setText(text);
        mailSender.send(message);
    }
}
