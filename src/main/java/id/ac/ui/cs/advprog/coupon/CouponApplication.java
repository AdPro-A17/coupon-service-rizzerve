package id.ac.ui.cs.advprog.coupon;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;



@SpringBootApplication
@EnableAsync
public class CouponApplication {

	public static void main(String[] args) {
		// Load .env file before Spring Boot starts
		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			dotenv.entries().forEach(e -> {
				System.setProperty(e.getKey(), e.getValue());
			});
		} catch (Exception e) {
			System.err.println("Error loading .env file: " + e.getMessage());
		}

		SpringApplication.run(CouponApplication.class, args);
	}

}
