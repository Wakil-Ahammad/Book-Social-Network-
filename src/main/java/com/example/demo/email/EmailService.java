package com.example.demo.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.*;
import static org.springframework.mail.javamail.MimeMessageHelper.*;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to,
                          String userName,
                          EmailTemplateName emailTemplateName,
                          String confirmationUrl,
                          String activationCode,
                          String subject
    ) throws MessagingException {

       String templateName;
       if(emailTemplateName == null) {
          templateName = "confirm-email";
       } else {
           templateName = emailTemplateName.name();
       }

       MimeMessage mimeMessage = mailSender.createMimeMessage();
       MimeMessageHelper helper = new MimeMessageHelper(
               mimeMessage,
               MULTIPART_MODE_MIXED,
               UTF_8.name()
       );

       Map<String, Object> properties = new HashMap<>();
       properties.put("username", userName);
       properties.put("confirmationUrl", confirmationUrl);
       properties.put("activation_Code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("ahmedwakil545@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);

    }
}
