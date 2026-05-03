package com.github.mohrezal.worker.services.email;

import com.github.mohrezal.worker.config.WorkerProperties;
import jakarta.mail.MessagingException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class SmtpEmailProvider implements EmailProvider {

    private final WorkerProperties workerProperties;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void send(
            String to, String subject, String templatePath, Map<String, Object> variables) {
        try {
            var context = new Context();
            context.setVariables(variables);
            var htmlContent = templateEngine.process(templatePath, context);

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(workerProperties.mail().from());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
