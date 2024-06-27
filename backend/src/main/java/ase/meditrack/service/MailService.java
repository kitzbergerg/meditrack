package ase.meditrack.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailService {
    private final JavaMailSender emailSender;

    @Autowired
    public MailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * Send a mail to the given address with the given subject and text.
     *
     * @param to the address to send the mail to
     * @param subject the subject of the mail
     * @param text the content of the mail
     */
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom("MediTrack <meditrack.ase@gmail.com>");
        mail.setSubject(subject);
        mail.setText(text);
        log.info("Sending mail to {} with subject: {}", to, subject);
        emailSender.send(mail);
    }

    /**
     * Send a mail to the given addresses with the given subject and text.
     *
     * @param to the addresses to send the mails to
     * @param subject the subject of the mail
     * @param text the content of the mail
     */
    public void sendSimpleMessages(List<String> to, String subject, String text) {
        for (String address : to) {
            sendSimpleMessage(address, subject, text);
        }
    }
}
