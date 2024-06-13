package Controllers.UserController;

import entities.Reset;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VerifController implements Initializable {




        public String email ;
        @FXML
        private TextField code;
        @FXML
        private Text mqte;

        /**
         * Initializes the controller class.
         */
        @Override
        public void initialize(URL url, ResourceBundle rb) {
            // TODO
        }

        @FXML
        private void Verif(ActionEvent event) throws IOException {
            UserService uss = new UserService();
            if (code.getText().equals("")) {
                mqte.setText("Champ Manquant");
            } else if (uss.reset(new Reset(Integer.parseInt(code.getText())))) {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface/ModifierMdp.fxml"));
                Parent root = loader.load();
                code.getScene().setRoot(root);

                ModifierMdpController apc = loader.getController();
                apc.setTxt1(email);

                // Afficher une alerte de confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Code Vérifié");
                alert.setHeaderText(null);
                alert.setContentText("Le code a été vérifié avec succès.");

                // Attendre que l'utilisateur appuie sur le bouton OK
                alert.showAndWait();

            } else {
                mqte.setText("Une erreur est survenue");
            }
        }
    }


