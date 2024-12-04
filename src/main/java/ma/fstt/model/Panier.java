package ma.fstt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "panier")
@Data
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Changed from internaute_id to user_id
    private User user; // Changed from internaute to user

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL)
    private List<LignePanier> lignesPanier;
}
