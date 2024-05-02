package com.sofrecom.mailingmicroservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sofrecom.mailingmicroservice.dto.CancellingDTO;
import com.sofrecom.mailingmicroservice.entity.Mail;
import com.sofrecom.mailingmicroservice.repository.MailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailCancelledServiceTest {

    private MailCancelledService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private MailRepository mailRepository;

    @InjectMocks
    private MailCancelledService mailCancelledService;

    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        emailService = new MailCancelledService(javaMailSender, mailRepository);
    }

    @Test
    void listen() {
    }

    //When the JSON is valid and contains the "cause" field, it should return the value of that field.
    @Test
    void buildEmailContent_ValidJson_ReturnsCause() throws Exception {

        String cancellingJson = "{\"cause\":\"Some cause\"}";
        CancellingDTO cancellingDTO = new CancellingDTO();
        cancellingDTO.setCause("Some cause");

        String emailContent = mailCancelledService.buildEmailContent(cancellingJson);

        assertEquals("Some cause", emailContent);
    }

    //When the JSON is invalid or cannot be parsed, it should return the default value.
    @Test
    void buildEmailContent_InvalidJson_ReturnsDefault() {

        String cancellingJson = "{\"invalid\":\"json\"}";

        String emailContent = mailCancelledService.buildEmailContent(cancellingJson);

        assertEquals("le content n'existe pas", emailContent);
    }

    //When the JSON is missing the "cause" field, it should also return the default value.
    @Test
    void buildEmailContent_MissingCause_ReturnsDefault() {

        String cancellingJson = "{}";

        String emailContent = mailCancelledService.buildEmailContent(cancellingJson);

        assertEquals("le content n'existe pas", emailContent);
    }

    @Test
    public void sendEmail_Successful() {
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String text = "Test Email Content";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(mailRepository, times(1)).save(any(Mail.class));
    }

    @Test
    public void sendEmail_Failure() {
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String text = "Test Email Content";

        doThrow(new MailSendException("Failed to send email")).when(javaMailSender).send(any(SimpleMailMessage.class));
        emailService.sendEmail(to, subject, text);

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
        verify(mailRepository, never()).save(any(Mail.class));
    }
}