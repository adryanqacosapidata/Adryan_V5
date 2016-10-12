package com.adryan.app.mail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by vquispe on 16/10/2015.
 */
public class GMailSender {
    private Multipart _multipart = new MimeMultipart();

    static {
        Security.addProvider(new com.adryan.app.mail.JSSEProvider());
    }

    public GMailSender() {

    }

    public synchronized void sendMail(String subject, String body, String recipents) throws Exception{
        Session session;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        try {
            session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("victor.quispe.adryan@gmail.com", "v1ct0r201088");
                }
            });

            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            BodyPart messageBodyPart = new MimeBodyPart();

            message.setSender(new InternetAddress("victor.quispe.adryan@gmail.com"));
            message.setFrom(new InternetAddress("victor.quispe.adryan@gmail.com"));
            message.setSubject(subject);
            message.setDataHandler(handler);
            messageBodyPart.setText(body);
            _multipart.addBodyPart(messageBodyPart);
            message.setContent(_multipart);

            if (recipents.indexOf(',') > 0){
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipents));
            } else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipents));
            }

            Transport.send(message);
        } catch (MessagingException ex) {
            //Log.e("ERROR", ex.getMessage());
            ex.printStackTrace();
        } catch (Exception e) {
            //Log.e("ERROR", e.getMessage());
            e.printStackTrace();
        }
    }

    public void addAttachment(String path,String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(path);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }

    }
}