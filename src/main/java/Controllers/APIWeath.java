package Controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;
public class APIWeath {
//dossier zeyed


    private static final String API_KEY = "5f01184843765e0bdeac9fc1bfa4649f"; // Replace with your API key

    public static void getWeatherForecast(LocalDate date) {
        try {
            // Format the date to the required format by the weather API
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = date.format(formatter);


            // Construct the URL for the weather API
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=44.34&lon=10.99&appid=" +API_KEY ;

            // Send HTTP request to the API
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Extract weather information from the response
            JSONObject forecast = jsonResponse.getJSONObject("forecast");
            JSONArray forecastday = forecast.getJSONArray("forecastday");
            JSONObject dayForecast = forecastday.getJSONObject(0); // Assuming you only need the first day
            JSONObject day = dayForecast.getJSONObject("day");

            // Display weather information
            String condition = day.getString("condition");
            double maxTempC = day.getDouble("maxtemp_c");
            double minTempC = day.getDouble("mintemp_c");

            System.out.println("Weather forecast for " + formattedDate + ":");
            System.out.println("Condition: " + condition);
            System.out.println("Max Temperature: " + maxTempC + "°C");
            System.out.println("Min Temperature: " + minTempC + "°C");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
