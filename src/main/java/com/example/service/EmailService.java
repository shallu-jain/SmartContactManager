package com.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String toEmail, String subject, String message) {
        boolean f = false;
        try {

            String from = "190303105122@paruluniversity.ac.in";
            // variable for gmail
            String host = "smtp.gmail.com";

            // get the system properties
            Properties properties = System.getProperties();
            System.out.println("Properties : " + properties);
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "587");
           // properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.starttls.enable","true");
            properties.put("mail.smtp.starttls.required","true");
            properties.put("mail.smtp.auth", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("190303105122@paruluniversity.ac.in", "JShallu6153@");
                }
            });
            session.setDebug(true);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(from);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);
            f = true;
            System.out.println("Email Sent Successfully!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
}
