package mainPackage.tmanager.services;

import mainPackage.tmanager.models.User;
import mainPackage.tmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public void save(User user){
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }


    public List<User> findAllByUsers(List<User> users) {
        return userRepository.findAllById(users);
    }


}
