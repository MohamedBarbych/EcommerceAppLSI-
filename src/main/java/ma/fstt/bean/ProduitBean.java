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

    /**
     * Save or update a product in the database.
     */
    public void saveProduit() {
        try {
            em.getTransaction().begin();
            if (produit.getId() != null) {
                em.merge(produit); // Update if the product already exists
            } else {
                em.persist(produit); // Add a new product
            }
            em.getTransaction().commit();
            produit = new Produit(); // Reset the product object for new entries
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the list of products from the database.
     *
     * @return a list of products.
     */
    public List<Produit> getProduits() {
        if (produits == null) {
            produits = em.createQuery("SELECT p FROM Produit p", Produit.class).getResultList();
        }
        return produits;
    }
}
