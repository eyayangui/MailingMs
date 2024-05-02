package com.sofrecom.mailingmicroservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.mailingmicroservice.dto.BookingDTO;
import com.sofrecom.mailingmicroservice.dto.CancellingDTO;
import com.sofrecom.mailingmicroservice.entity.Mail;
import com.sofrecom.mailingmicroservice.proxy.CancellingServiceClient;
import com.sofrecom.mailingmicroservice.proxy.UserServiceClient;
import com.sofrecom.mailingmicroservice.repository.MailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailCancelledService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private CancellingServiceClient cancellingServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    MailCancelledService(JavaMailSender javaMailSender, MailRepository mailRepository){
        this.javaMailSender = javaMailSender;
        this.mailRepository = mailRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @KafkaListener(topics = "cancelledMail", groupId = "mailingServiceGroup")
    public void listen(String cancellingJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CancellingDTO cancellingDTO = objectMapper.readValue(cancellingJson, CancellingDTO.class);

            BookingDTO bookingDTO = cancellingServiceClient.getBookingDTOByIdCancelling(cancellingDTO.getIdCancelling());

            String driverEmail = String.valueOf(userServiceClient.getCollaboratorEmailById(bookingDTO.getDriverId())).trim();

            System.out.println("Passenger Email: " + driverEmail);

            String emailContent = buildEmailContent(cancellingJson);

            sendEmail(driverEmail, "Cancelled Reservation", emailContent);
        } catch (Exception e) {
            logger.error("Error processing booking notification: {}", e.getMessage(), e);
        }
    }


    public String buildEmailContent(String cancellingJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CancellingDTO cancellingDTO = objectMapper.readValue(cancellingJson, CancellingDTO.class);
            String cause = cancellingDTO.getCause();
            return cause != null ? cause : "le content n'existe pas";
        } catch (Exception e) {
            logger.error("Error processing booking notification: {}", e.getMessage(), e);
            return "le content n'existe pas";
        }
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
