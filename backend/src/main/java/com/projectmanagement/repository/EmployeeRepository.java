package com.projectmanagement.repository;

import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Employee
 * Fournit les opérations CRUD et les requêtes personnalisées pour les employés
 * 
 * Hérite de JpaRepository pour accéder aux méthodes CRUD standards :
 * - save(Employee)
 * - findById(Long)
 * - findAll()
 * - delete(Employee)
 * - deleteById(Long)
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Recherche un employé par son email unique
     * Utilisé principalement pour l'authentification (login)
     * 
     * @param email l'email de l'employé
     * @return Optional<Employee> contenant l'employé si trouvé
     * 
     * Exemple : findByEmail("john.doe@company.com")
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Vérifie si un email est déjà utilisé
     * Utile pour valider l'unicité de l'email avant création/modification
     * 
     * @param email l'email à vérifier
     * @return true si l'email existe, false sinon
     * 
     * Exemple : existsByEmail("alice@company.com")
     */
    boolean existsByEmail(String email);

    /**
     * Récupère tous les employés d'une catégorie spécifique
     * Permet de voir les employés par domaine (Developer, Manager, etc.)
     * 
     * @param categoryId l'ID de la catégorie
     * @return List<Employee> contenant tous les employés de cette catégorie
     * 
     * Exemple : findByCategoryId(1L)
     */
    List<Employee> findByCategoryId(Long categoryId);

    /**
     * Récupère tous les employés d'une catégorie, triés par nom
     * 
     * @param categoryId l'ID de la catégorie
     * @return List<Employee> triée par nom (firstName + lastName)
     */
    @Query("SELECT e FROM Employee e WHERE e.category.id = :categoryId ORDER BY e.firstName, e.lastName")
    List<Employee> findByCategoryIdOrderByName(@Param("categoryId") Long categoryId);

    /**
     * Récupère tous les employés ayant un rôle spécifique
     * Permet de filtrer par ADMIN ou EMPLOYEE
     * 
     * @param role le rôle à rechercher
     * @return List<Employee> contenant tous les employés ayant ce rôle
     * 
     * Exemple : findByRole(UserRole.ADMIN)
     */
    List<Employee> findByRole(UserRole role);

    /**
     * Récupère le nombre d'employés par rôle
     * Utile pour les statistiques
     * 
     * @param role le rôle
     * @return le nombre d'employés ayant ce rôle
     */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.role = :role")
    long countByRole(@Param("role") UserRole role);

    /**
     * Recherche les employés dont le nom complet contient le terme de recherche (insensible à la casse)
     * Permet une recherche flexible par prénom ou nom
     * 
     * @param searchTerm le terme de recherche
     * @return List<Employee> contenant les employés correspondants
     * 
     * Exemple : searchByFullName("john") → retourne les employés avec "john" dans prénom ou nom
     */
    @Query("SELECT e FROM Employee e WHERE LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employee> searchByFullName(@Param("searchTerm") String searchTerm);

    /**
     * Obtient tous les employés avec leurs informations de catégorie
     * Requête optimisée avec JOIN FETCH pour éviter N+1 queries
     * 
     * @return List<Employee> avec les catégories préchargées
     */
    @Query("SELECT DISTINCT e FROM Employee e JOIN FETCH e.category")
    List<Employee> findAllWithCategory();

    /**
     * Obtient tous les employés avec leurs affectations
     * Requête optimisée avec JOIN FETCH pour éviter N+1 queries
     * 
     * @return List<Employee> avec les affectations préchargées
     */
    @Query("SELECT DISTINCT e FROM Employee e LEFT JOIN FETCH e.assignments")
    List<Employee> findAllWithAssignments();

    /**
     * Obtient un employé avec toutes ses relations (category et assignments)
     * Utile pour une vue détaillée d'un employé
     * 
     * @param id l'ID de l'employé
     * @return Optional<Employee> complètement chargé
     */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.assignments LEFT JOIN FETCH e.category WHERE e.id = :id")
    Optional<Employee> findByIdWithRelations(@Param("id") Long id);

    /**
     * Recherche les employés actuellement affectés à un projet spécifique
     * 
     * @param projectId l'ID du projet
     * @return List<Employee> affectés à ce projet
     */
    @Query("SELECT DISTINCT e FROM Employee e JOIN e.assignments a WHERE a.project.id = :projectId")
    List<Employee> findByProjectId(@Param("projectId") Long projectId);

    /**
     * Recherche les employés d'une catégorie spécifique affectés à un projet
     * 
     * @param categoryId l'ID de la catégorie
     * @param projectId l'ID du projet
     * @return List<Employee> affectés au projet et dans la catégorie
     */
    @Query("SELECT DISTINCT e FROM Employee e JOIN e.assignments a WHERE e.category.id = :categoryId AND a.project.id = :projectId")
    List<Employee> findByCategoryAndProject(@Param("categoryId") Long categoryId, @Param("projectId") Long projectId);

    /**
     * Obtient le nombre total d'employés
     * 
     * @return le nombre total d'employés
     */
    @Query("SELECT COUNT(e) FROM Employee e")
    long countTotalEmployees();

    /**
     * Récupère les employés triés par nom complet
     * 
     * @return List<Employee> triée par firstName puis lastName
     */
    @Query("SELECT e FROM Employee e ORDER BY e.firstName, e.lastName")
    List<Employee> findAllOrderedByName();

    /**
     * Récupère les employés avec le nombre d'affectations actives
     * Utile pour voir la charge de travail des employés
     * 
     * @param employeeId l'ID de l'employé
     * @return le nombre d'affectations actives (date actuelle entre startDate et endDate)
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.employee.id = :employeeId AND CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    long countActiveAssignments(@Param("employeeId") Long employeeId);

    /**
     * Vérifie si un email existe (excluant un employé spécifique)
     * Utile lors de la modification pour s'assurer que l'email reste unique
     * 
     * @param email l'email à vérifier
     * @param excludeEmployeeId l'ID de l'employé à exclure
     * @return true si l'email existe ailleurs
     */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.email = :email AND e.id != :excludeEmployeeId")
    boolean existsEmailExcluding(@Param("email") String email, @Param("excludeEmployeeId") Long excludeEmployeeId);
}
