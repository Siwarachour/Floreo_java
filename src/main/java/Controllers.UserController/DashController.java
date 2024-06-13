package Controllers.UserController;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.AnchorPane;
import services.UserService;

import java.util.Map;
public class DashController {




        @FXML
        private AnchorPane rootPane;

        @FXML
        private BarChart<String, Number> barChart;

        @FXML
        private CategoryAxis xAxis;

        @FXML
        private NumberAxis yAxis;
    @FXML
    private AreaChart<String, Number> areaChart;



        @FXML
        private CategoryAxis xAxisArea;

        @FXML
        private NumberAxis yAxisArea;


        @FXML
        private void initialize() {
            // Récupérer les données pour les graphiques
            Map<String, Integer> dataForBarChart = retrieveDataForBarChart();
            Map<String, Integer> dataForAreaChart = retrieveDataForAreaChart();

            // Configurer les axes pour le graphique à barres
            xAxis.setLabel("Roles");
            yAxis.setLabel("Number of Users");

            // Configurer les axes pour le graphique de zone
            xAxisArea.setLabel("Number Of Users");
            yAxisArea.setLabel("Age");

            // Ajouter les données au graphique à barres
            XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();
            dataForBarChart.forEach((role, count) -> {
                seriesBar.getData().add(new XYChart.Data<>(role, count));
            });
            barChart.getData().add(seriesBar);

            // Ajouter les données au graphique de zone
            XYChart.Series<String, Number> seriesArea = new XYChart.Series<>();
            dataForAreaChart.forEach((label, value) -> {
                seriesArea.getData().add(new XYChart.Data<>(label, value));
            });
            areaChart.getData().add(seriesArea);

        }

        // Méthode de récupération des données pour le graphique à barres
        private Map<String, Integer> retrieveDataForBarChart() {
            // Implémentez votre logique de récupération des données ici
            // Par exemple :
             UserService userService = new UserService();
             return userService.getUserCountByRole();
            // Assurez-vous de retourner un Map<String, Integer> avec les données nécessaires pour le graphique à barres.
            // Remplacez ceci par votre propre logique de récupération des données
        }

        // Méthode de récupération des données pour le graphique de zone
        private Map<String, Integer> retrieveDataForAreaChart() {
            // Implémentez votre logique de récupération des données ici
            // Par exemple :
             UserService userService = new UserService();
            return userService.getDataForAreaChart();
            // Assurez-vous de retourner un Map<String, Integer> avec les données nécessaires pour le graphique de zone.
            // Remplacez ceci par votre propre logique de récupération des données

    }

}


