package ma.fstt.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "produit")
@Data
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false)
    private int stock;
}
