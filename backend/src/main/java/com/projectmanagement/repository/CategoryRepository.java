package com.projectmanagement.repository;

import com.projectmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Category
 * Fournit les opérations CRUD et les requêtes personnalisées pour les catégories
 * 
 * Hérite de JpaRepository pour accéder aux méthodes CRUD standards :
 * - save(Category)
 * - findById(Long)
 * - findAll()
 * - delete(Category)
 * - deleteById(Long)
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Recherche une catégorie par son nom unique
     * 
     * @param name le nom de la catégorie
     * @return Optional<Category> contenant la catégorie si trouvée
     * 
     * Exemple : findByName("Developer")
     */
    Optional<Category> findByName(String name);

    /**
     * Vérifie si une catégorie avec ce nom existe déjà
     * Utile pour valider l'unicité avant création/modification
     * 
     * @param name le nom de la catégorie
     * @return true si la catégorie existe, false sinon
     * 
     * Exemple : existsByName("Manager")
     */
    boolean existsByName(String name);

    /**
     * Recherche les catégories dont le nom contient la chaîne spécifiée (insensible à la casse)
     * Permet une recherche flexible dans les catégories
     * 
     * @param searchTerm le terme de recherche
     * @return List<Category> contenant les catégories correspondantes
     * 
     * Exemple : findByNameContainingIgnoreCase("dev") → retourne "Developer", "DevOps"
     */
    List<Category> findByNameContainingIgnoreCase(String searchTerm);

    /**
     * Requête personnalisée pour obtenir le nombre total de catégories
     * 
     * @return le nombre total de catégories
     */
    @Query("SELECT COUNT(c) FROM Category c")
    long countAllCategories();

    /**
     * Requête personnalisée pour obtenir toutes les catégories triées par nom
     * Utile pour afficher les catégories dans un ordre alphabétique
     * 
     * @return List<Category> triée par nom (A-Z)
     */
    @Query("SELECT c FROM Category c ORDER BY c.name ASC")
    List<Category> findAllOrderedByName();

    /**
     * Obtient les catégories qui ont au moins N employés
     * Utile pour identifier les catégories actives ou populaires
     * 
     * @param minEmployeeCount le nombre minimum d'employés
     * @return List<Category> contenant les catégories avec au moins N employés
     * 
     * Exemple : findCategoriesWithMinEmployees(5) → retourne les catégories avec 5+ employés
     */
    @Query("SELECT c FROM Category c WHERE SIZE(c.employees) >= :minCount")
    List<Category> findCategoriesWithMinEmployees(@Param("minCount") int minEmployeeCount);

    /**
     * Obtient le nombre d'employés par catégorie
     * Utile pour générer des statistiques
     * 
     * @param categoryId l'ID de la catégorie
     * @return le nombre d'employés dans cette catégorie
     */
    @Query("SELECT COUNT(e) FROM Category c JOIN c.employees e WHERE c.id = :categoryId")
    long countEmployeesByCategory(@Param("categoryId") Long categoryId);
}
