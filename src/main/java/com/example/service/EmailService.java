package com.example.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService{
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${baseUrl}")
    private String baseUrl;


    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public String sendSimpleMail(String username,String email,Integer index) {

        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        try {

            String url=baseUrl+"/api/v1/auth/verify-email?email="+email+"&type=";

            String subject =
                    "Email Verification";
            String senderName
                    =
                    "FinTrack";
            List<String> simple= new ArrayList<>();
            simple.add("<p> Hi, "+ username+"</p>"+
                    "<p>Thank you for registering with us</p>"+
                    "Please, verify your account with link  below to complete your registration.</p>"+
                    "<div>  "+"<a  href="+url+"1"+"  ><h3> verify email </h3></a>"+" </div>\n" +
                    "<p> Thank you </p>");

            simple.add("<p> Hi, "+username+ "</p>"+
                    "Please, verify your account with link  below to complete your reset Password.</p>"+
                    "<p>Here link for verify email for set new password</p>"+
                    "<div> "+"<a  href="+url+"2"+"  ><h3> verify email </h3></a>"+" </div>\n" +
                    "<p> Thank you </p>");


            String html = "<!doctype html>\n" +
                    "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                    "      xmlns:th=\"http://www.thymeleaf.org\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\"\n" +
                    "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                    "    <title>Email</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    simple.get(index-1)+
                    "</body>\n" +
                    "</html>\n";

            MimeMessage message = javaMailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper (message) ;

            messageHelper.setFrom( sender, senderName);
            messageHelper.setTo(email);
            messageHelper.setSubject(subject);
            messageHelper.setText(html,true) ;
            javaMailSender.send (message) ;

            return "Mail Successfully...";
        } catch (Exception e) {
            return "Error while Sending Mail";
        }
    }
}
