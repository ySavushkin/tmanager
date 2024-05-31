package mainPackage.tmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mainPackage.tmanager.enums.UserRoleInProjectE;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data //Генерирует геттеры, сеттеры, методы toString(), equals() и hashCode() для всех полей класса.
@Builder //Генерирует паттерн строителя для создания объектов с несколькими полями, обеспечивая гибкость в конструировании объектов.
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Username should be not null")
    @NotEmpty(message = "Username field can't be empty")
    @Column(name = "username")
    private String username;

    @NotNull(message = "Email should be not null")
    @NotEmpty(message = "Email field can't be empty")
    @Email(message = "The email you provided is not valid")
    @Column(name = "email")
    private String email;

    @NotNull(message = "Provide password")
    @NotEmpty(message = "Provide password")
    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

    public User(String username, String email, String encode) {
        this.username = username;
        this.email = email;
        this.password = encode;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }

    public Set<Role> getRoles() {
        return roles;
    }
}