package com.example.mall_demo;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void sendEmail(String recipientEmail, String subject, String content) {
        // 这里为了保护我的邮箱安全，已将邮箱与密码隐藏起来，若有其他人需要fork，请将自己的邮箱与密码替换之。
        final String username = "example@gmail.com";
        final String password = "password";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail)
            );
            message.setSubject("Bug_Mall：确认订单");
            message.setText("Bug_Mall 官方来跟您确认商品订单了！");
            Transport.send(message);
            System.out.println("发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}