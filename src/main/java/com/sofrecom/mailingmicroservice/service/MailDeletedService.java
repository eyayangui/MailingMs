package com.sofrecom.mailingmicroservice.service;

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

import java.util.List;

@Service
public class MailDeletedService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private CancellingServiceClient cancellingServiceClient;


    @Autowired
    private UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @KafkaListener(topics = "deletedMail", groupId = "mailingServiceGroup")
    public void listen(String announcementId) {
        try {
            List<Integer> userIds = cancellingServiceClient.getUserIdsByAnnouncementId(Long.valueOf(announcementId));

            for (Integer userId : userIds) {
                String userEmail = String.valueOf(userServiceClient.getCollaboratorEmailById(userId)).trim();

                String emailContent = buildEmailContent();

                sendEmail(userEmail, "Deleted Reservation", emailContent);
            }

        } catch (Exception e) {
            logger.error("Error processing booking notification: {}", e.getMessage(), e);
        }
    }

    public String buildEmailContent() {
        String emailContent = "Je suis vraiment désolé, mais je dois annuler le covoiturage prévu. ";
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
