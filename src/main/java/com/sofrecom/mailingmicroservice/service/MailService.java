package com.sofrecom.mailingmicroservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.mailingmicroservice.dto.BookingDTO;
import com.sofrecom.mailingmicroservice.entity.Mail;
import com.sofrecom.mailingmicroservice.proxy.UserServiceClient;
import com.sofrecom.mailingmicroservice.repository.MailRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;


@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @KafkaListener(topics = "mailNotification", groupId = "mailingServiceGroup")
    public void listen(String bookingJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            BookingDTO bookingDTO = objectMapper.readValue(bookingJson, BookingDTO.class);

            String driverEmail = String.valueOf(userServiceClient.getCollaboratorEmailById(bookingDTO.getDriverId())).trim();

            System.out.println("Driver Email: " + driverEmail);

            String emailContent = buildEmailContent();

            sendEmail(driverEmail, "New Reservation", emailContent);
        } catch (Exception e) {
            logger.error("Error processing booking notification: {}", e.getMessage(), e);
        }
    }

    public String buildEmailContent() {

        String emailContent = "Booking Details:\n" ;

        return emailContent;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sof@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
            javaMailSender.send(message);
            logger.info("Email sent to: {}", to);

            Mail email = new Mail();
            email.setSender(message.getFrom());
            email.setReceiver(to);
            email.setSubject(subject);
            email.setText(text);
            mailRepository.save(email);

        } catch (MailException e) {
            logger.error("Error sending email to: {}, Error: {}", to, e.getMessage(), e);
        }
    }



}
