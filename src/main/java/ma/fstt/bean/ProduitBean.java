package ma.fstt.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;
import ma.fstt.model.Produit;

import java.util.List;

@Named
@RequestScoped
@Data
public class ProduitBean {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private EntityManager em = emf.createEntityManager();

    private Produit produit = new Produit(); // The product being managed
    private List<Produit> produits; // List of all products

    public void saveProduit() {
        em.getTransaction().begin();
        em.persist(produit);
        em.getTransaction().commit();
        produit = new Produit(); // Reset the product for the form
    }

    public List<Produit> getProduits() {
        if (produits == null) {
            produits = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();
        }
        return produits;
    }


}
