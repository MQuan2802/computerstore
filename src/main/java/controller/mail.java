package controller;

import javax.servlet.http.HttpServlet;
import java.io.*;

import javax.lang.model.util.ElementScanner14;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.*;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.*;
import javax.activation.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.StandardCharsets.*;

public class mail {
    String from = "ntmquan282@gmail.com";
    String host = "ahihi8.com";

    public void send(String mess, String email) {

        Properties properties = new Properties();

        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("cc@ahihi8.com", "436553");
            }
        });
        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Verificaton email");
            message.setContent(mess, "text/html");
            Transport.send(message);
        } catch (

        MessagingException mex) {
            mex.printStackTrace();
            ;
        }

    }

}
