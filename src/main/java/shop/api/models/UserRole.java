package shop.api.models;

import javax.validation.constraints.NotNull;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role Role;

    @NotNull
    @ManyToOne(fetch =  FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User User;
}