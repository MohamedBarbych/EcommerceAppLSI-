package ma.fstt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate; // Import pour la gestion des dates
import java.util.List;

@Entity
@Table(name = "user")
@Data // Lombok pour générer getters, setters, equals, hashCode, toString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Role: "admin" ou "client"

    @Column(nullable = true)
    private LocalDate dateDeNaissance; // Nouvelle colonne pour la date de naissance

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Panier> paniers;
}
