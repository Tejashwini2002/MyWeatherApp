package MyPackage;

import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		/*
		String inputData = request.getParameter("userInput");
		System.out.println(inputData); // This will be printed on the console
		*/
		//doGet(request, response);
		
		//API Setup
//		String apiKey = "Apni-Api-Key Dalo";
		String apiKey ="234089cd73e366ab42f1f4e8b67322ae";
		// Get the city from the form input
		String city = request.getParameter("city");
		//Create the URL for the OpenWeatherMap API request
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
		//API Integration
		
		//URL url = new URI(apiUrl).toURL();
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            //System.out.println(reader);
            
            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }
            
            // System.out.println(responseContent);
            scanner.close();
            //System.out.println(responseContent);  This will be printed on the console 
            /*
            // JSON format API Response it is. For example: {"coord":{"lon":75.0167,"lat":15.4667},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03n"}],"base":"stations","main":{"temp":298.81,"feels_like":299.6,"temp_min":298.81,"temp_max":298.81,"pressure":1012,"humidity":83,"sea_level":1012,"grnd_level":936},"visibility":6000,"wind":{"speed":2.57,"deg":250},"clouds":{"all":40},"dt":1733579520,"sys":{"type":1,"id":9222,"country":"IN","sunrise":1733534061,"sunset":1733574532},"timezone":19800,"id":1272818,"name":"Dharwad","cod":200}
		    JSON means JavaScript Object Notation. As you can see object within an object. In the above example sqare bracket [] represents an array and flower brackets {} represent an object.
		    */
            
            // Now, as the response is in JSON format, we need to convert it to String. So we'll do TypeCasting THAT IS also known as Parsing.
            
            // Parse the JSON response to extract temperature, date, and humidity
            // Gson is a library of Google.  
            /* Gson can be used to convert a JSON string to an equivalent Java object. It can also be used to convert Java Objects into their JSON representation. */
            /* When you try to import Gson and JsonObject just using import statement they'll not get imported because they are not present in JDK. So we will install it from Google externally and put in lib folder which is in WEB-INF folder.*/
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
//            System.out.println(jsonObject);
            
            //Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            String date = new Date(dateTimestamp).toString();
            
            //Temperature
            double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble(); /* Here "main" is an object from json object, which is having another object as value which has all those temperature, humidity,etc. that is this:- "main":{"temp":294.66,"feels_like":295.16,"temp_min":294.66,"temp_max":294.66,"pressure":1012,"humidity":88,"sea_level":1012,"grnd_level":937},"visibility":10000,"wind":{"speed":2.18,"deg":243,"gust":2.58},"clouds":{"all":100},"dt":1733592959*/
            int temperatureCelsius = (int) (temperatureKelvin - 273.15); // Formula to convert the temperature from Kelvin to Celsius.
           
            //Humidity
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            
            //Wind Speed
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
            
            //Weather Condition
            String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString(); // Here get(0) indicates value at 0th index //"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]
            
         // Set the data as request attributes (for sending to the jsp page)
            request.setAttribute("date", date);
            request.setAttribute("city", city);
            request.setAttribute("temperature", temperatureCelsius);
            request.setAttribute("weatherCondition", weatherCondition); 
            request.setAttribute("humidity", humidity);    
            request.setAttribute("windSpeed", windSpeed);
            request.setAttribute("weatherData", responseContent.toString());
            
            connection.disconnect();
            
         // Forward the request to the weather.jsp page for rendering
            request.getRequestDispatcher("index.jsp").forward(request, response);
            
            
            
            
            
	}

}
