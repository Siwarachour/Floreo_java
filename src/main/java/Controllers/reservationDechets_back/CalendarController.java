package Controllers.reservationDechets_back;

import entities.ReservationDechets;
import services.ReservationDechetsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

public class CalendarController implements Initializable {

    List<ReservationDechets> listReservationDechets;

    ZonedDateTime dateFocus;
    Color customColor = Color.web("#8fb43a");
    ZonedDateTime today;

    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Assuming ReservationDechets has a getDate() method returning LocalDate
        listReservationDechets = ReservationDechetsService.getInstance().getAll();
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    private void drawCalendar() {
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        Map<Integer, List<ReservationDechets>> reservationDechetsMap = getReservationDechetsMap();

        int monthMaxDate = dateFocus.getMonth().length(true);
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j + 1) + (7 * i);
                if (calculatedDate > dateOffset) {
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = -(rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        List<ReservationDechets> reservationDechetsList = reservationDechetsMap.get(currentDate);
                        if (reservationDechetsList != null && !reservationDechetsList.isEmpty()) {
                            createReservationDechets(reservationDechetsList, rectangleHeight, rectangleWidth, stackPane);
                            rectangle.setFill(customColor); // Set the box color to blue if there are reservations
                        }
                    }
                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        rectangle.setStroke(customColor);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void createReservationDechets(List<ReservationDechets> reservationDechetsList, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        for (ReservationDechets reservationDechets : reservationDechetsList) {
            Text reservationDechetsText = new Text(reservationDechets.ToDate());
            reservationDechetsText.setTranslateY((rectangleHeight / 2) * 0.20);
            stackPane.getChildren().add(reservationDechetsText);
        }
    }

    private Map<Integer, List<ReservationDechets>> getReservationDechetsMap() {
        Map<Integer, List<ReservationDechets>> reservationDechetsMap = new HashMap<>();

        for (ReservationDechets reservationDechets : listReservationDechets) {
            LocalDate reservationDate = reservationDechets.getDateRamassage();
            int dayOfMonth = reservationDate.getDayOfMonth();
            int monthValue = reservationDate.getMonthValue();
            int yearValue = reservationDate.getYear();

            // Check if the reservation date is in the same month and year as the dateFocus
            if (monthValue == dateFocus.getMonthValue() && yearValue == dateFocus.getYear()) {
                List<ReservationDechets> reservationDechetsList = reservationDechetsMap.getOrDefault(dayOfMonth, new ArrayList<>());
                reservationDechetsList.add(reservationDechets);
                reservationDechetsMap.put(dayOfMonth, reservationDechetsList);
            }
        }

        return reservationDechetsMap;
    }

}
