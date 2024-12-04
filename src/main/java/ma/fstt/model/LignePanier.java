package ma.fstt.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ligne_panier")
@Data
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "panier_id", nullable = false)
    private Panier panier;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private int quantite;
}