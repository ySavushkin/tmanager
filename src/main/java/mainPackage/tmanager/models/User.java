package mainPackage.tmanager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mainPackage.tmanager.enums.UserRoleInProject;

import java.time.LocalDateTime;
import java.util.List;

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
    private int id;

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

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRoleInProject role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

}

