package com.example.applepie.Service;

import android.util.Log;

import java.util.Properties;

import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message;
public class EmailSender {

    // Hàm gửi OTP qua email
    public static void sendOTP(String recipientEmail, String otpCode) {
        new Thread(() -> {
            String senderEmail = "lop9c.thd.ngqtrinh@gmail.com"; // thay bằng email thật
            String senderPassword = "guun lnuw pghr ftap"; // App Password

            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.socketFactory.port", "465");
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Mã OTP xác nhận đăng ký tài khoản Apple Pie");
                message.setText("Chào bạn,\n\nMã OTP Apple Pie của bạn là: " + otpCode + "\n\nVui lòng nhập mã này để xác nhận đăng ký của bạn.");

                Transport.send(message);
                Log.d("EmailSender", "Email đã được gửi thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("EmailSender", "Gửi email thất bại: " + e.toString());
            }
        }).start(); // <-- chạy trên thread riêng
    }
}

