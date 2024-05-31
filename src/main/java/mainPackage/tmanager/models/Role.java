package mainPackage.tmanager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mainPackage.tmanager.enums.UserRoleInProjectE;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRoleInProjectE name;

    public Role(UserRoleInProjectE name) {
        this.name = name;
    }
}
