package mainPackage.tmanager;

import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TmanagerApplication {
	private final UserService  userService;
	@Autowired
    public TmanagerApplication(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
		SpringApplication.run(TmanagerApplication.class, args);
		System.out.println("Hello tmanager");

	}

}
