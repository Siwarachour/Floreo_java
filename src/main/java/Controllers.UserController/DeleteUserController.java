package Controllers.UserController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class DeleteUserController {
    private int userId;
    private boolean isConfirmed = false;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnCancel;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    @FXML
    void confirmDelete(MouseEvent event) {
        isConfirmed = true;
        closeStage();
    }

    @FXML
    void cancelDelete(MouseEvent event) {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void start() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DeleteUser.fxml"));
            loader.setController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
