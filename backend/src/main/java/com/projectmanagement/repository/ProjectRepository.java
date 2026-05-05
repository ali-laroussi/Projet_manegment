package com.projectmanagement.repository;

import com.projectmanagement.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Project
 * Fournit les opérations CRUD et les requêtes personnalisées pour les projets
 * 
 * Hérite de JpaRepository pour accéder aux méthodes CRUD standards :
 * - save(Project)
 * - findById(Long)
 * - findAll()
 * - delete(Project)
 * - deleteById(Long)
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Recherche les projets dont le titre contient le terme spécifié (insensible à la casse)
     * Permet une recherche flexible dans les titres de projet
     * 
     * @param titleSearchTerm le terme de recherche
     * @return List<Project> contenant les projets correspondants
     * 
     * Exemple : findByTitleContainingIgnoreCase("e-commerce") 
     *          → retourne "E-Commerce Platform", "e-commerce-api", etc.
     */
    List<Project> findByTitleContainingIgnoreCase(String titleSearchTerm);

    /**
     * Recherche les projets dont la description contient le terme spécifié (insensible à la casse)
     * 
     * @param descriptionSearchTerm le terme de recherche
     * @return List<Project> contenant les projets correspondants
     * 
     * Exemple : findByDescriptionContainingIgnoreCase("angular")
     */
    List<Project> findByDescriptionContainingIgnoreCase(String descriptionSearchTerm);

    /**
     * Obtient tous les projets dont la date de début est avant la date spécifiée
     * Utile pour trouver les projets qui ont commencé
     * 
     * @param date la date limite
     * @return List<Project> ayant commencé avant cette date
     * 
     * Exemple : findByStartDateBefore(LocalDate.now()) → projets déjà commencés
     */
    List<Project> findByStartDateBefore(LocalDate date);

    /**
     * Obtient tous les projets dont la date de fin est après la date spécifiée
     * Utile pour trouver les projets futures ou en cours
     * 
     * @param date la date limite
     * @return List<Project> se terminant après cette date
     */
    List<Project> findByEndDateAfter(LocalDate date);

    /**
     * Obtient les projets actuellement actifs (entre startDate et endDate)
     * 
     * @return List<Project> actuellement en cours
     * 
     * Exemple : findActiveProjects() → retourne tous les projets actuels
     */
    @Query("SELECT p FROM Project p WHERE CURRENT_DATE BETWEEN p.startDate AND p.endDate ORDER BY p.endDate")
    List<Project> findActiveProjects();

    /**
     * Obtient les projets qui ne sont pas encore commencés
     * Utile pour afficher les projets futurs
     * 
     * @return List<Project> non encore commencés
     */
    @Query("SELECT p FROM Project p WHERE p.startDate > CURRENT_DATE ORDER BY p.startDate")
    List<Project> findUpcomingProjects();

    /**
     * Obtient les projets terminés (date de fin passée)
     * Utile pour afficher l'historique des projets
     * 
     * @return List<Project> terminés
     */
    @Query("SELECT p FROM Project p WHERE p.endDate < CURRENT_DATE ORDER BY p.endDate DESC")
    List<Project> findCompletedProjects();

    /**
     * Obtient les projets compris dans une plage de dates
     * Utile pour filtrer par période
     * 
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return List<Project> dans la plage de dates spécifiée
     */
    @Query("SELECT p FROM Project p WHERE p.startDate >= :startDate AND p.endDate <= :endDate")
    List<Project> findProjectsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Obtient tous les projets avec leurs affectations
     * Requête optimisée avec JOIN FETCH pour éviter N+1 queries
     * 
     * @return List<Project> avec les affectations préchargées
     */
    @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.assignments")
    List<Project> findAllWithAssignments();

    /**
     * Obtient un projet avec toutes ses affectations et employés associés
     * Utile pour une vue détaillée d'un projet
     * 
     * @param id l'ID du projet
     * @return Optional<Project> complètement chargé
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.assignments WHERE p.id = :id")
    Optional<Project> findByIdWithAssignments(@Param("id") Long id);

    /**
     * Obtient le nombre d'employés affectés à un projet
     * 
     * @param projectId l'ID du projet
     * @return le nombre d'employés uniques affectés
     */
    @Query("SELECT COUNT(DISTINCT a.employee.id) FROM Assignment a WHERE a.project.id = :projectId")
    long countEmployeesByProject(@Param("projectId") Long projectId);

    /**
     * Obtient le nombre d'affectations actives pour un projet
     * 
     * @param projectId l'ID du projet
     * @return le nombre d'affectations actuellement actives
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.project.id = :projectId AND CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    long countActiveAssignmentsByProject(@Param("projectId") Long projectId);

    /**
     * Recherche les projets auxquels un employé est affecté
     * 
     * @param employeeId l'ID de l'employé
     * @return List<Project> auxquels l'employé est affecté
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.assignments a WHERE a.employee.id = :employeeId")
    List<Project> findByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Recherche les projets actuellement actifs auxquels un employé est affecté
     * 
     * @param employeeId l'ID de l'employé
     * @return List<Project> actuellement actifs pour cet employé
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.assignments a WHERE a.employee.id = :employeeId AND CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    List<Project> findActiveProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Obtient tous les projets triés par date de début (ordre croissant)
     * 
     * @return List<Project> triée par startDate
     */
    @Query("SELECT p FROM Project p ORDER BY p.startDate ASC")
    List<Project> findAllOrderedByStartDate();

    /**
     * Obtient le nombre total de projets
     * 
     * @return le nombre total de projets
     */
    @Query("SELECT COUNT(p) FROM Project p")
    long countTotalProjects();

    /**
     * Obtient le nombre de projets actuellement actifs
     * 
     * @return le nombre de projets actifs
     */
    @Query("SELECT COUNT(p) FROM Project p WHERE CURRENT_DATE BETWEEN p.startDate AND p.endDate")
    long countActiveProjects();

    /**
     * Recherche les projets dont la durée (endDate - startDate) est supérieure à N jours
     * Utile pour filtrer par durée du projet
     * 
     * @param minDays la durée minimale en jours
     * @return List<Project> avec une durée d'au moins N jours
     */
    @Query("SELECT p FROM Project p WHERE DATEDIFF(p.endDate, p.startDate) >= :minDays")
    List<Project> findProjectsWithMinDuration(@Param("minDays") int minDays);

    /**
     * Vérifie si un titre de projet existe
     * Utile pour valider l'unicité (optionnel selon les besoins)
     * 
     * @param title le titre à vérifier
     * @return true si le titre existe
     */
    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE LOWER(p.title) = LOWER(:title)")
    boolean existsByTitleIgnoreCase(@Param("title") String title);
}
