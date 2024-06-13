package Controllers.UserController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;


public class ForgotController {
    @FXML
    private ImageView retour;

    @FXML
    private TextField textField;

    int randomCode;
    public void retourClick(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface/Registration.fxml"));
            Scene scene = retour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private boolean isValidEmail(String email) {
        // Vérifier si l'e-mail est dans un format valide (vous pouvez utiliser votre propre logique de validation ici)
        // Retourner true si l'e-mail est valide, sinon false
        // Par exemple, vous pouvez utiliser une expression régulière pour valider l'e-mail
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @FXML
    private void envoyerMailMdp(ActionEvent event) throws SQLException, AddressException, MessagingException {

        try{


            Random rand =new Random();
            randomCode = rand.nextInt(999999);
            String host = "smtp.gmail.com";
            String user = "2409b9b847085b";
            String pass="543d725197949a";
            String to =textField.getText();
            String sujet ="reset pot de passe ";
            String message="your reset code is "+randomCode;
            boolean sessionDebug =  false;
            Properties pros = System.getProperties();
            pros.put("mail.smtp.starttls.enable","true");
            pros.put("mail.smtp.host","pop3.mailtrap.io");
            pros.put("mail.smtp.port", "9950");
            pros.put("mail.smtp.auth", "true");
            pros.put("mail.smtp.starttls.required", "true");
         //   java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(pros,null);
            mailSession.setDebug(sessionDebug);
            Message msg =new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(user));
            InternetAddress [] address = { new InternetAddress(to) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(sujet);
            msg.setText(message);
            Transport transport = mailSession.getTransport("smtp");
            transport.connect(host,user,pass);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
            JOptionPane.showMessageDialog(null, "code envoye a la boite mail");
        }catch(Exception ex){
            Component rootPane = null;
            JOptionPane.showMessageDialog(rootPane,ex);
            System.out.println(ex.getMessage());

        }



    }




    private String generateResetToken() {
        // Générez un jeton de réinitialisation unique ici (par exemple, un UUID)
        // Vous pouvez utiliser des bibliothèques comme java.util.UUID pour générer un UUID aléatoire
        return UUID.randomUUID().toString();
    }
}
