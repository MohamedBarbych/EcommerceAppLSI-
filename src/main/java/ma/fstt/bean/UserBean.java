package ma.fstt.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.Data;
import ma.fstt.model.User;

import java.util.List;

@Named
@RequestScoped
@Data
public class UserBean {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("ecommercePU");
    private EntityManager em = emf.createEntityManager();

    private User user = new User(); // Utilisateur en cours (pour les formulaires)
    private List<User> users; // Liste des utilisateurs
    private String searchQuery = ""; // Requête pour la recherche dynamique
    private String loginMessage; // Message de connexion

    private Long totalUsers; // Statistique : nombre total d'utilisateurs
    private Long adminCount; // Statistique : nombre d'administrateurs
    private Long clientCount; // Statistique : nombre de clients

    // Recherche dynamique des utilisateurs
    public List<User> getUsers() {
        if (searchQuery == null || searchQuery.isEmpty()) {
            users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } else {
            users = em.createQuery("SELECT u FROM User u WHERE u.nom LIKE :searchQuery", User.class)
                    .setParameter("searchQuery", "%" + searchQuery + "%")
                    .getResultList();
        }
        return users;
    }

    // Inscription d'un nouveau client (rôle "client") et redirection vers la page de connexion
    public String registerClient() {
        try {
            user.setRole("client"); // Définit le rôle comme client
            em.getTransaction().begin();
            em.persist(user); // Enregistre le nouvel utilisateur
            em.getTransaction().commit();
            user = new User(); // Réinitialise l'utilisateur pour le formulaire

            // Redirection vers la page de connexion
            return "/index.xhtml?faces-redirect=true";
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null; // Rester sur la page d'inscription en cas d'erreur
        }
    }


    // Ajouter un nouvel administrateur
    public String addAdmin() {
        try {
            user.setRole("admin"); // Rôle défini comme administrateur
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            user = new User(); // Réinitialiser l'utilisateur
            return "/admin/adminDashboard.xhtml?faces-redirect=true"; // Redirection vers le tableau de bord admin
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
    }

    // Préparer la modification d'un utilisateur
    public String prepareEdit(User selectedUser) {
        this.user = selectedUser; // Remplir les données de l'utilisateur sélectionné
        return "/admin/editClient.xhtml?faces-redirect=true"; // Rediriger vers la page de modification
    }

    // Sauvegarder ou mettre à jour un utilisateur (client ou admin) par un administrateur
    public String saveOrUpdateUser() {
        try {
            em.getTransaction().begin();
            if (user.getId() != null) {
                em.merge(user); // Mise à jour
            } else {
                em.persist(user); // Ajout
            }
            em.getTransaction().commit();
            user = new User(); // Réinitialiser le formulaire
            return "/admin/adminDashboard.xhtml?faces-redirect=true"; // Redirection vers le tableau de bord admin
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return null; // Rester sur la même page en cas d'erreur
        }
    }

    // Supprimer un utilisateur
    public void deleteUser(Long userId) {
        try {
            em.getTransaction().begin();
            User userToDelete = em.find(User.class, userId);
            if (userToDelete != null) {
                em.remove(userToDelete);
            }
            em.getTransaction().commit();
            loadUsers(); // Rafraîchir la liste des utilisateurs
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    // Charger les statistiques
    public void loadStatistics() {
        totalUsers = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
        adminCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'admin'", Long.class).getSingleResult();
        clientCount = em.createQuery("SELECT COUNT(u) FROM User u WHERE u.role = 'client'", Long.class).getSingleResult();
    }

    // Méthode d'authentification
    public String authenticate() {
        try {
            User authenticatedUser = em.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", user.getEmail())
                    .setParameter("password", user.getPassword())
                    .getSingleResult();

            if ("admin".equals(authenticatedUser.getRole())) {
                return "/admin/adminDashboard.xhtml?faces-redirect=true";
            } else {
                return "/client/clientDashboard.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            loginMessage = "Email ou mot de passe invalide.";
            return null; // Rester sur la page de connexion en cas d'échec
        }
    }

    // Chargement des utilisateurs
    public void loadUsers() {
        getUsers();
    }

    // Getters pour les statistiques
    public Long getTotalUsers() {
        if (totalUsers == null) loadStatistics();
        return totalUsers;
    }

    public Long getAdminCount() {
        if (adminCount == null) loadStatistics();
        return adminCount;
    }

    public Long getClientCount() {
        if (clientCount == null) loadStatistics();
        return clientCount;
    }
}
