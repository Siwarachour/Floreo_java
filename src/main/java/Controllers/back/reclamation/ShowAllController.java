package Controllers.back.reclamation;

import Controllers.UserController.SideBarController;
import Controllers.back.reponse.ManageController;
import entities.Reclamation;
import entities.User;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReclamationService;
import utils.AlertUtils;
import utils.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class ShowAllController implements Initializable {

    public static Reclamation currentReclamation;

    @FXML
    public Text topText;
    @FXML
    public VBox mainVBox;
    @FXML
    public ComboBox<String> sortCB;
    @FXML
    public TextField searchTF;
    public static User user;
    List<Reclamation> listReclamation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listReclamation = ReclamationService.getInstance().getAll();

        sortCB.getItems().addAll(
                "Tri par type",
                "Tri par description",
                "Tri par dateajout",
                "Tri par datemodif",
                "Tri par user"
        );

        displayData("");
    }

    void displayData(String searchText) {
        mainVBox.getChildren().clear();

        Collections.reverse(listReclamation);

        listReclamation.stream()
                .filter(
                        reclamation -> searchText.isEmpty()
                                || reclamation.getType().contains(searchText)
                                || reclamation.getDescription().contains(searchText)
                                || (reclamation.getUser().toString() != null && reclamation.getUser().toString().contains(searchText))
                )
                .map(this::makeReclamationModel)
                .forEach(mainVBox.getChildren()::add);

        if (listReclamation.isEmpty()) {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }

    public Parent makeReclamationModel(
            Reclamation reclamation
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_RECLAMATION)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#typeText")).setText("Type : " + reclamation.getType());
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + reclamation.getDescription());
            ((Text) innerContainer.lookup("#dateajoutText")).setText("Dateajout : " + reclamation.getDateajout());
            ((Text) innerContainer.lookup("#datemodifText")).setText("Datemodif : " + reclamation.getDatemodif());

            ((Text) innerContainer.lookup("#userText")).setText("User : " + reclamation.getUser().getName());


            ((Button) innerContainer.lookup("#repButton")).setOnAction((ignored) -> repondreReclamation(reclamation));
            ((Button) innerContainer.lookup("#editButton")).setOnAction((ignored) -> modifierReclamation(reclamation));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((ignored) -> supprimerReclamation(reclamation));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void repondreReclamation(Reclamation reclamation) {
        ManageController.selectedReclamation = reclamation;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_REPONSE));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le nouvel interface
            Scene scene = new Scene(root);

            // Créer un nouveau stage pour afficher la nouvelle scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Titre de la nouvelle fenêtre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierReclamation(Reclamation reclamation) {
        currentReclamation = reclamation;
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_MANAGE_RECLAMATION));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le nouvel interface
            Scene scene = new Scene(root);

            // Créer un nouveau stage pour afficher la nouvelle scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Titre de la nouvelle fenêtre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerReclamation(Reclamation reclamation) {
        currentReclamation = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer reclamation ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (ReclamationService.getInstance().delete(reclamation.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_RECLAMATION));
                        Parent root = loader.load();

                        // Créer une nouvelle scène avec le nouvel interface
                        Scene scene = new Scene(root);

                        // Créer un nouveau stage pour afficher la nouvelle scène
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.setTitle("Titre de la nouvelle fenêtre");
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertUtils.errorNotification("Could not delete reclamation");
                }
            }
        }
    }

    @FXML
    public void sort(ActionEvent actionEvent) {
        Reclamation.compareVar = sortCB.getValue();
        listReclamation.sort(Reclamation::compareTo);
        displayData(searchTF.getText() == null ? "" : searchTF.getText());
    }

    public void search(KeyEvent keyEvent) {
        displayData(searchTF.getText());
    }

    public void generateExcel(ActionEvent ignored) {
        String fileName = "reclamation.xls";

        HSSFWorkbook workbook = new HSSFWorkbook();

        try {
            FileChooser chooser = new FileChooser();
            // Set extension filter
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Excel Files(.xls)", ".xls");
            chooser.getExtensionFilters().add(filter);

            HSSFSheet workSheet = workbook.createSheet("sheet 0");
            workSheet.setColumnWidth(1, 25);

            HSSFFont fontBold = workbook.createFont();
            HSSFCellStyle styleBold = workbook.createCellStyle();
            styleBold.setFont(fontBold);

            Row row1 = workSheet.createRow((short) 0);
            workSheet.autoSizeColumn(7);
            row1.createCell(0).setCellValue("Id");
            row1.createCell(1).setCellValue("Type");
            row1.createCell(2).setCellValue("Description");
            row1.createCell(3).setCellValue("Dateajout");
            row1.createCell(4).setCellValue("Datemodif");
            row1.createCell(5).setCellValue("User");

            int i = 0;
            for (Reclamation reclamation : listReclamation) {
                i++;
                Row row2 = workSheet.createRow((short) i);

                row2.createCell(0).setCellValue(reclamation.getId());
                row2.createCell(1).setCellValue(reclamation.getType());
                row2.createCell(2).setCellValue(reclamation.getDescription());
                row2.createCell(3).setCellValue(reclamation.getDateajout().toString());
                row2.createCell(4).setCellValue(reclamation.getDatemodif().toString());
                row2.createCell(5).setCellValue(reclamation.getUser() != null ? reclamation.getUser().toString() : "");
            }

            workbook.write(Files.newOutputStream(Paths.get(fileName)));
            Desktop.getDesktop().open(new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openStats(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML du nouvel interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_STATS_RECLAMATION));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le nouvel interface
            Scene scene = new Scene(root);

            // Créer un nouveau stage pour afficher la nouvelle scène
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Titre de la nouvelle fenêtre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
