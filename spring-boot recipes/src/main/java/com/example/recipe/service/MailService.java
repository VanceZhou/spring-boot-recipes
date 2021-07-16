package com.example.recipe.service;

import com.example.recipe.exceptions.recipeEmailSendingException;
import com.example.recipe.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
//handle log.info
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail){
// somehow adding this configuration in properties file not working, so I need to set up manually
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.mailtrap.io");
        mailSender.setPort(2525);
        mailSender.setUsername("4debc152810388");
        mailSender.setPassword("3cde8f2cc03751");
        mailSender.setProtocol("smtp");

//        getting info from notificationEmail input to build a message.
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("recipeOfficial@email.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
//        try to send this message after preparation.
        try{
            mailSender.send(messagePreparator);
            log.info("Activation email sent!");
        } catch (MailException e){
            throw new recipeEmailSendingException("exception occurred when sending email!");
        }
    }
}
