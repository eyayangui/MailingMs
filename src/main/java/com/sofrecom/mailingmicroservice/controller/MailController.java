package com.sofrecom.mailingmicroservice.controller;

import com.sofrecom.mailingmicroservice.entity.Mail;
import com.sofrecom.mailingmicroservice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MailController {
    @Autowired
    private MailService mailService;

    /*@PostMapping("/sendMail")
    public String sendMail(@RequestBody Mail request) {
        String to = request.getReceiverEmail();
        String subject = "Confirmation de réservation de covoiturage";
        String text = "Votre réservation a été confirmée avec succès.";

        return mailService.sendMail(to, subject, text);
    }*/
    @Autowired
    private MailService mailingService;

    @GetMapping("/send")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String text) {
        mailingService.sendEmail(to, subject, text);
        return "Email sent to " + to;
    }
}
