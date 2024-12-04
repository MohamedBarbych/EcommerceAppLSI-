package ma.fstt.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;
import ma.fstt.model.LignePanier;
import ma.fstt.model.Panier;
import ma.fstt.model.Produit;

import java.util.List;

@Named
@RequestScoped
@Data
public class LignePanierBean {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private EntityManager em = emf.createEntityManager();

    private LignePanier lignePanier = new LignePanier(); // The cart line item being managed
    private List<LignePanier> lignesPanier; // List of all cart line items

    private Panier panier; // The cart associated with the line item
    private Produit produit; // The product associated with the line item

    public void addLignePanier() {
        em.getTransaction().begin();
        lignePanier.setPanier(panier);
        lignePanier.setProduit(produit);
        em.persist(lignePanier);
        em.getTransaction().commit();
        lignePanier = new LignePanier(); // Reset the line item for the form
    }

    public List<LignePanier> getLignesPanier() {
        if (lignesPanier == null) {
            lignesPanier = em.createQuery("SELECT lp FROM LignePanier lp WHERE lp.panier = :panier", LignePanier.class)
                    .setParameter("panier", panier)
                    .getResultList();
        }
        return lignesPanier;
    }


}
