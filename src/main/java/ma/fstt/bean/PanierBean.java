package ma.fstt.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;
import ma.fstt.model.Panier;
import ma.fstt.model.User;

import java.util.List;

@Named  // Injection
@RequestScoped
@Data
public class PanierBean {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private EntityManager em = emf.createEntityManager();

    private Panier panier = new Panier(); // The cart being managed
    private List<Panier> paniers; // List of all carts

    private User user; // User associated with the cart (can be retrieved from the session)

    public void addPanier() {
        em.getTransaction().begin();
        panier.setUser(user); // Set the user who owns the cart
        em.persist(panier);
        em.getTransaction().commit();
        panier = new Panier(); // Reset the cart for the form
    }

    public List<Panier> getPaniers() {
        if (paniers == null) {
            paniers = em.createQuery("SELECT p FROM Panier p WHERE p.user = :user", Panier.class)
                    .setParameter("user", user)
                    .getResultList();
        }
        return paniers;
    }


}
