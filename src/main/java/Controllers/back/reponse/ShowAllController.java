package Controllers.back.reponse;

import entities.Reponse;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.ReponseService;
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

    public static Reponse currentReponse;

    @FXML
    public Text topText;
    @FXML
    public VBox mainVBox;
    @FXML
    public ComboBox<String> sortCB;
    @FXML
    public TextField searchTF;


    List<Reponse> listReponse;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listReponse = ReponseService.getInstance().getAll();

        sortCB.getItems().addAll(
                "Tri par description",
                "Tri par date ajout",
                "Tri par date modif"
        );

        displayData("");
    }

    void displayData(String searchText) {
        mainVBox.getChildren().clear();

        Collections.reverse(listReponse);

        listReponse.stream()
                .filter(reponse -> searchText.isEmpty()
                        || reponse.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .map(this::makeReponseModel)
                .forEach(mainVBox.getChildren()::add);

        if (listReponse.isEmpty()) {
            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.setPrefHeight(200);
            stackPane.getChildren().add(new Text("Aucune donnée"));
            mainVBox.getChildren().add(stackPane);
        }
    }


    public Parent makeReponseModel(
            Reponse reponse
    ) {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(Constants.FXML_BACK_MODEL_REPONSE)));

            HBox innerContainer = ((HBox) ((AnchorPane) ((AnchorPane) parent).getChildren().get(0)).getChildren().get(0));
            ((Text) innerContainer.lookup("#descriptionText")).setText("Description : " + reponse.getDescription());
            ((Text) innerContainer.lookup("#dateajoutText")).setText("Dateajout : " + reponse.getDateajout());
            ((Text) innerContainer.lookup("#datemodifText")).setText("Datemodif : " + reponse.getDatemodif());
            ((Text) innerContainer.lookup("#reclamationText")).setText("Reclamation : " + reponse.getReclamation().toString());

            ((Button) innerContainer.lookup("#editButton")).setOnAction((ignored) -> modifierReponse(reponse));
            ((Button) innerContainer.lookup("#deleteButton")).setOnAction((ignored) -> supprimerReponse(reponse));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return parent;
    }

    private void modifierReponse(Reponse reponse) {
        currentReponse = reponse;
        ManageController.selectedReclamation = null;
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

    private void supprimerReponse(Reponse reponse) {
        ManageController.selectedReclamation = null;

        currentReponse = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmer la suppression");
        alert.setHeaderText(null);
        alert.setContentText("Etes vous sûr de vouloir supprimer reponse ?");
        Optional<ButtonType> action = alert.showAndWait();

        if (action.isPresent()) {
            if (action.get() == ButtonType.OK) {
                if (ReponseService.getInstance().delete(reponse.getId())) {
                    try {
                        // Charger le fichier FXML du nouvel interface
                        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.FXML_BACK_DISPLAY_ALL_REPONSE));
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
                    AlertUtils.errorNotification("Could not delete reponse");
                }
            }
        }
    }


    @FXML
    public void sort(ActionEvent actionEvent) {
        Reponse.compareVar = sortCB.getValue();
        listReponse.sort(Reponse::compareTo);
        displayData(searchTF.getText() == null ? "" : searchTF.getText());
    }

    @FXML
    public void search(KeyEvent keyEvent) {
        displayData(searchTF.getText());
    }

    public void generateExcel(ActionEvent ignored) {
        String fileName = "reponse.xls";

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
            row1.createCell(1).setCellValue("Description");
            row1.createCell(2).setCellValue("Dateajout");
            row1.createCell(3).setCellValue("Datemodif");

            int i = 0;
            for (Reponse reponse : listReponse) {
                i++;
                Row row2 = workSheet.createRow((short) i);

                row2.createCell(0).setCellValue(reponse.getId());
                row2.createCell(1).setCellValue(reponse.getDescription());
                row2.createCell(2).setCellValue(String.valueOf(reponse.getDateajout()));
                row2.createCell(3).setCellValue(String.valueOf(reponse.getDatemodif()));
            }

            workbook.write(Files.newOutputStream(Paths.get(fileName)));
            Desktop.getDesktop().open(new File(fileName));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
