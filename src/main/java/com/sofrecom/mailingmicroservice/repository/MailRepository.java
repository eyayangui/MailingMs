package com.sofrecom.mailingmicroservice.repository;

import com.sofrecom.mailingmicroservice.entity.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mail, Long> {
}
