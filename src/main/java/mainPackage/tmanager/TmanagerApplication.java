package mainPackage.tmanager;

import mainPackage.tmanager.models.User;
import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TmanagerApplication.class, args);
		System.out.println("Hello tmanager");


	}
}
