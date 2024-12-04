package ma.fstt.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import ma.fstt.model.Panier;
import ma.fstt.model.Produit;
import ma.fstt.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseInitializer {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private static EntityManager em = emf.createEntityManager();

    public static void initializeDatabase() {
        try {
            em.getTransaction().begin();

            // Formatter pour les dates
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Créer un utilisateur Admin
            User adminUser = new User();
            adminUser.setEmail("manager@ecommerce.com");
            adminUser.setNom("Manager Central");
            adminUser.setPassword("admin2024"); // Mot de passe (hash sécurisé en production)
            adminUser.setRole("admin"); // Définir le rôle "admin"
            adminUser.setDateDeNaissance(LocalDate.parse("10/04/1985", dateFormatter)); // Date de naissance

            // Persister l'utilisateur Admin
            em.persist(adminUser);

            // Créer un utilisateur Client
            User clientUser = new User();
            clientUser.setEmail("client@shop.com");
            clientUser.setNom("Client User");
            clientUser.setPassword("password2024"); // Mot de passe (hash sécurisé en production)
            clientUser.setRole("client"); // Définir le rôle "client"
            clientUser.setDateDeNaissance(LocalDate.parse("22/11/1990", dateFormatter)); // Date de naissance

            // Persister l'utilisateur Client
            em.persist(clientUser);

            // Créer un Panier pour le Client User
            Panier clientPanier = new Panier();
            clientPanier.setUser(clientUser); // Lier le panier à l'utilisateur client

            // Persister l'entité Panier
            em.persist(clientPanier);

            // Créer des produits
            Produit produit1 = new Produit();
            produit1.setNom("Ordinateur Portable");
            produit1.setPrix(1200.0);
            produit1.setStock(25);
            em.persist(produit1);

            Produit produit2 = new Produit();
            produit2.setNom("Télévision HD");
            produit2.setPrix(1500.0);
            produit2.setStock(15);
            em.persist(produit2);

            Produit produit3 = new Produit();
            produit3.setNom("Casque Bluetooth");
            produit3.setPrix(200.0);
            produit3.setStock(100);
            em.persist(produit3);

            // Commit de la transaction
            em.getTransaction().commit();

            System.out.println("Base de données initialisée avec succès avec utilisateurs, rôles, et produits.");

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
    }
}
