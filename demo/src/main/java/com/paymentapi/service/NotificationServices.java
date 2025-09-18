package com.paymentapi.service;

import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.paymentapi.model.PendingEvent;
import com.paymentapi.repositry.PendingEventRepositry;

@Service
public class NotificationServices {
    private final PendingEventRepositry pendingEventRepositry;

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.port}")
    private String port;
    @Value("${spring.mail.fromEmail}")
    private String fromEmail;
    
    public NotificationServices(PendingEventRepositry pendingEventRepositry) {
        this.pendingEventRepositry = pendingEventRepositry;
    }

    @Scheduled(fixedDelay = 60000)
    public void processPendingEvent() {
        System.out.println("in schedule task");
        List<PendingEvent> pendingEvents = pendingEventRepositry.findAll();

        for (PendingEvent event : pendingEvents) {
            System.out.println("processing event" + event.getId());
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(port));
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", String.valueOf(port));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(event.getEmail()));
                message.setSubject("Stripe Payment Status");
                message.setText("Your Payment Status: " + event.getStatus()+"\n with payment id :"+event.getPaymentIntentId()+"\n with payment id :"+event.getEventId());

                Transport.send(message);
                //delete that event info
                pendingEventRepositry.deleteById(event.getId());
                System.out.println("Email send successfully");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("email failed, event id :" + event.getEventId());
            }
        }
    }
}
