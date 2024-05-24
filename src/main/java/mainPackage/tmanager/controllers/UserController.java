package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            userService.save(user);
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id){
        Optional<User> user = userService.findById(id);

        return ResponseEntity.ok(user);
    }
}
