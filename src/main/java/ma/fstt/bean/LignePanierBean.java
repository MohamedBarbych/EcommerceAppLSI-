package ma.fstt.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;
import ma.fstt.model.LignePanier;
import ma.fstt.model.Panier;
import ma.fstt.model.Produit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Data
public class LignePanierBean implements Serializable {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private EntityManager em = emf.createEntityManager();

    private LignePanier lignePanier = new LignePanier(); // Current cart line
    private List<LignePanier> lignesPanier = new ArrayList<>(); // All cart lines
    private Panier panier = new Panier(); // Current cart (should be tied to a user)
    private Produit selectedProduit; // Selected product for the cart line

    // Add a product to the cart
    public void addLignePanier(Produit produit) {
        if (produit == null) {
            System.out.println("No product selected!");
            return;
        }

        try {
            em.getTransaction().begin();

            // Check if the product is already in the cart
            LignePanier existingLine = em.createQuery("SELECT lp FROM LignePanier lp WHERE lp.produit.id = :produitId AND lp.panier.id = :panierId", LignePanier.class)
                    .setParameter("produitId", produit.getId())
                    .setParameter("panierId", panier.getId())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingLine != null) {
                // Update the quantity
                existingLine.setQuantite(existingLine.getQuantite() + 1);
                em.merge(existingLine);
            } else {
                // Add new line item
                LignePanier newLine = new LignePanier();
                newLine.setPanier(panier);
                newLine.setProduit(produit);
                newLine.setQuantite(1);
                em.persist(newLine);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Fetch all line items for the current cart
    public List<LignePanier> getLignesPanier() {
        if (lignesPanier.isEmpty()) {
            lignesPanier = em.createQuery("SELECT lp FROM LignePanier lp WHERE lp.panier = :panier", LignePanier.class)
                    .setParameter("panier", panier)
                    .getResultList();
        }
        return lignesPanier;
    }

    // Remove a product from the cart
    public void removeLignePanier(LignePanier lignePanier) {
        try {
            em.getTransaction().begin();
            em.remove(em.contains(lignePanier) ? lignePanier : em.merge(lignePanier));
            lignesPanier.remove(lignePanier); // Update the in-memory list
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Clear the cart
    public void clearCart() {
        try {
            em.getTransaction().begin();
            for (LignePanier lp : lignesPanier) {
                em.remove(em.contains(lp) ? lp : em.merge(lp));
            }
            lignesPanier.clear(); // Clear the in-memory list
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
