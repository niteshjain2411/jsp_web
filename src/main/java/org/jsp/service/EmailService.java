package org.jsp.service;

import org.jsp.model.ContactFormRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String DESTINATION_EMAIL = "jainsanghpune@gmail.com";

    public void sendContactInquiry(ContactFormRequest request) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setTo(DESTINATION_EMAIL);
        mailMessage.setReplyTo(request.getEmail());
        mailMessage.setSubject("New Enquiry: " + request.getFullName() + " via Jain Sangh Pune Website");

        String bodyText = String.format(
                "You have received a new contact inquiry:\n\n" +
                        "Full Name: %s\n" +
                        "Email: %s\n" +
                        "Phone: %s\n\n" +
                        "Message Details:\n%s\n",
                request.getFullName(),
                request.getEmail(),
                (request.getPhone() != null && !request.getPhone().isBlank()) ? request.getPhone() : "Not Provided",
                request.getMessage()
        );

        mailMessage.setText(bodyText);
        mailSender.send(mailMessage);
    }
}