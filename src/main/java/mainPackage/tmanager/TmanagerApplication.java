package mainPackage.tmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmanagerApplication.class, args);
		System.out.println("Hello tmanager");
	}

}
