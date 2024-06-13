package Controllers.UserController;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mail.Sendmail;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static Controllers.UserController.ResetPwdController.sEmail;

public class ModifierMdpController implements Initializable {

        @FXML
        private PasswordField Nvmdp;
        @FXML
        private PasswordField cnfMdp;
        @FXML
        private Text txt1;
        @FXML
        private Text txt;

        public Text getTxt1() {
            return txt1;
        }

        public void setTxt1(String txt1) {
            this.txt1.setText(txt1);
        }
        /**
         * Initializes the controller class.
         */
        @Override
        public void initialize(URL url, ResourceBundle rb) {
            // TODO

            txt1.setText(sEmail.toString());
            System.out.println("1:"+txt.getText()+"2:"+sEmail);
        }
        @FXML
        private void SubmitMdp(ActionEvent event) throws IOException, SQLException {
            Sendmail sn = new Sendmail();
            String Obj = "Reset Password";
            String Subject = "Bonjour " + txt1.getText() + " Votre mot de passe a été modifié avec succès";
            UserService uss = new UserService();
            if ((Nvmdp.getText().equals("")) && (cnfMdp.getText().equals(""))) {
                txt.setText("Champ Manquant");
            } else if (!Nvmdp.getText().equals(cnfMdp.getText())) {
                txt.setText("Password Non Compatible");
            } else {
                uss.modifierMdp(new User(sEmail, cnfMdp.getText()));
                txt.setText("Password Modifié");

                sn.envoyer(sEmail, Obj, Subject);

                // Afficher une boîte de dialogue de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Modification de mot de passe");
                alert.setHeaderText(null);
                alert.setContentText("Votre mot de passe a été modifié avec succès.");

                // Attendre que l'utilisateur appuie sur le bouton OK
                alert.showAndWait();

                // Redirection vers la page de connexion
                Parent page2 = FXMLLoader.load(getClass().getResource("/UserInterface/Login.fxml"));
                Scene scene2 = new Scene(page2);
                Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                app_stage.setScene(scene2);
                app_stage.show();
            }
        }


}
