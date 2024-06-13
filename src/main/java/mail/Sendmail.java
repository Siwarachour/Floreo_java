package mail;


import services.imResetPassword;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Sendmail extends imResetPassword {
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    Properties props = System.getProperties();
    final String username = "siwarachour999@gmail.com";
    final String password = "orwnlwxvngkerhig";


    public void envoyer(String Toemail, String Subject , String Object) {

        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable","true");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");
        try {
            Session session = Session.getDefaultInstance(props,
                new Authenticator() {

                    @Override
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }

                });
            // -- Create a new message --
            Message msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(Toemail, false));
            msg.setSubject(Subject);
            msg.setText(Object);
            msg.setSentDate(new Date());
            Transport.send(msg);
            System.out.println("Message sent.");
        } catch (MessagingException e) {
            System.out.println("Erreur d'envoi, cause: " + e);
        }


    }

    public void envoyerQr(String toEmail, String subject, String text, String imagePath) throws IOException {
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable","true");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.store.protocol", "pop3");
        props.put("mail.transport.protocol", "smtp");
        try {
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(username, password);
                }
            });
            // Create the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            message.setSubject(subject);

            // Create a multipart message
            MimeMultipart multipart = new MimeMultipart("related");

            // Create the text part of the message
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text, "utf-8", "html");
            multipart.addBodyPart(textPart);

            // Create the image part of the message
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.attachFile(imagePath);
            imagePart.setContentID("<image>");
            multipart.addBodyPart(imagePart);

            // Set the content of the message to the multipart
            message.setContent(multipart);

            // Send the message
            Transport.send(message);
            System.out.println("Message sent.");
        } catch (MessagingException | IOException e) {
            System.out.println("Erreur d'envoi, cause: " + e);
        }
    }


    // private static HashMap<String, String> CONF;

//    private static Properties PROPS;
//    private static Session SESSION;
//    final String username = "Maaha.messaoud@esprit.tn";
//    final String password = "223JFT2905";
//
//    public Sendmail() {
//
//        //CONF = MailConfig.getInstance().getConfig();
//        PROPS = new Properties();
//
//        PROPS.setProperty("mail.smtp.auth", "true");
//        PROPS.setProperty("mail.smtp.starttls.enable", "true");
//        PROPS.setProperty("mail.smtp.host", username);
//        PROPS.setProperty("mail.smtp.port", password);
//        PROPS.setProperty("mail.default-encoding", "UTF-8");
//
//        SESSION = Session.getDefaultInstance(PROPS, new Authenticator() {
//
//            @Override
//            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
//                return new javax.mail.PasswordAuthentication(username, password);
//            }
//
//        });
//    }
//
//    public void sendTextMail(String subject, String to, String body) {
//
//        System.out.println("Preparing to send email...");
//
//        try {
//
//            MimeMessage textMail = createTextMessage(to, subject, body);
//
//            Transport.send(textMail);
//            System.out.println("Message sent successfully !!");
//
//        } catch (MessagingException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//    }
//
//    private MimeMessage createTextMessage(String to, String subject, String body) throws MessagingException {
//
//        MimeMessage message = prepareMessage(to, subject);
//
//        message.setSentDate(new Date());
//
//        message.setText(body);
//
//        return message;
//    }
//
//    private MimeMessage prepareMessage(String to, String subject) throws AddressException, MessagingException {
//        MimeMessage message = new MimeMessage(SESSION);
//
//        message.setFrom(new InternetAddress(username));
//
//        // Set To: header field of the header.
//        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
//
//        // Set Subject: header field
//        message.setSubject(subject);
//
//        return message;
//    }
//    /*
//    public void sendMailWithAttachment(String subject, String to, String body, String filePath, String fileName) {
//
//	System.out.println("Preparing to send email...");
//
//	try {
//
//	    Message attMail = createMimeMessage(to, subject, body, filePath, fileName);
//
//	    Transport.send(attMail);
//
//	    System.out.println("Message sent successfully !!");
//
//	} catch (MessagingException | IOException ex) {
//	    System.out.println(ex.getMessage());
//	}
//    }
//
//    private Message createMimeMessage(String to, String subject, String body, String filePath, String fileName) throws MessagingException, IOException {
//
//	SESSION.setDebug(true);
//
//	MimeMessage message = prepareMessage(to, subject);
//
//	message.setSentDate(new Date());
//
//        MimeBodyPart bodyPart = new MimeBodyPart();
//        bodyPart.setContent("<h1>"+ body +"</h1>", "text/html; charset=utf-8");
//
//	MimeBodyPart attachmentPart = new MimeBodyPart();
//	attachmentPart.attachFile(new File(filePath, fileName));
//
//	Multipart multipart = new MimeMultipart();
//	multipart.addBodyPart(bodyPart);
//	multipart.addBodyPart(attachmentPart);
//
//	message.setContent(multipart);
//
//	return message;
//    }
//     */
}
