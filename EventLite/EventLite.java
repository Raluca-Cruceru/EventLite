package uk.ac.man.cs.eventlite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EventLite {
	
	
	private static String GOOGLE_API_KEY = "AIzaSyDtAqCBjMVqg24-4R2jGEV460wMJfUDi7o";


	public static void main(String[] args) {
		SpringApplication.run(EventLite.class, args);
	}

	public static String getGOOGLE_API_KEY() {
		return GOOGLE_API_KEY;

	}
}
