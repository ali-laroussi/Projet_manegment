package com.projectmanagement.repository;

import com.projectmanagement.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Assignment
 * Fournit les opérations CRUD et les requêtes personnalisées pour les affectations
 * 
 * Hérite de JpaRepository pour accéder aux méthodes CRUD standards :
 * - save(Assignment)
 * - findById(Long)
 * - findAll()
 * - delete(Assignment)
 * - deleteById(Long)
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    /**
     * Récupère toutes les affectations d'un employé spécifique
     * Permet de voir tous les projets auquel un employé est affecté
     * 
     * @param employeeId l'ID de l'employé
     * @return List<Assignment> de cet employé
     * 
     * Exemple : findByEmployeeId(1L)
     */
    List<Assignment> findByEmployeeId(Long employeeId);

    /**
     * Récupère toutes les affectations d'un employé triées par date de fin
     * Utile pour afficher les affectations dans l'ordre chronologique
     * 
     * @param employeeId l'ID de l'employé
     * @return List<Assignment> triée par endDate
     */
    @Query("SELECT a FROM Assignment a WHERE a.employee.id = :employeeId ORDER BY a.endDate DESC")
    List<Assignment> findByEmployeeIdOrderByEndDate(@Param("employeeId") Long employeeId);

    /**
     * Récupère toutes les affectations d'un projet spécifique
     * Permet de voir tous les employés affectés à un projet
     * 
     * @param projectId l'ID du projet
     * @return List<Assignment> pour ce projet
     * 
     * Exemple : findByProjectId(2L)
     */
    List<Assignment> findByProjectId(Long projectId);

    /**
     * Récupère toutes les affectations d'un projet triées par employé
     * 
     * @param projectId l'ID du projet
     * @return List<Assignment> triée par nom d'employé
     */
    @Query("SELECT a FROM Assignment a WHERE a.project.id = :projectId ORDER BY a.employee.firstName, a.employee.lastName")
    List<Assignment> findByProjectIdOrderByEmployee(@Param("projectId") Long projectId);

    /**
     * Récupère l'affectation spécifique d'un employé à un projet
     * Permet de vérifier si un employé est affecté à un projet
     * 
     * @param employeeId l'ID de l'employé
     * @param projectId l'ID du projet
     * @return Optional<Assignment> si l'affectation existe
     * 
     * Exemple : findByEmployeeIdAndProjectId(1L, 2L)
     */
    Optional<Assignment> findByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    /**
     * Vérifie si une affectation existe entre un employé et un projet
     * 
     * @param employeeId l'ID de l'employé
     * @param projectId l'ID du projet
     * @return true si l'affectation existe
     */
    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    /**
     * Récupère les affectations actuellement actives d'un employé
     * (dates actuelles entre startDate et endDate)
     * 
     * @param employeeId l'ID de l'employé
     * @return List<Assignment> actuellement actives
     * 
     * Exemple : findActiveAssignmentsByEmployeeId(1L)
     */
    @Query("SELECT a FROM Assignment a WHERE a.employee.id = :employeeId AND CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    List<Assignment> findActiveAssignmentsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Récupère les affectations actuellement actives d'un projet
     * 
     * @param projectId l'ID du projet
     * @return List<Assignment> actuellement actives pour ce projet
     */
    @Query("SELECT a FROM Assignment a WHERE a.project.id = :projectId AND CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    List<Assignment> findActiveAssignmentsByProjectId(@Param("projectId") Long projectId);

    /**
     * Récupère toutes les affectations actuellement actives
     * Utile pour un vue globale du travail en cours
     * 
     * @return List<Assignment> toutes les affectations actives
     */
    @Query("SELECT a FROM Assignment a WHERE CURRENT_DATE BETWEEN a.startDate AND a.endDate ORDER BY a.endDate")
    List<Assignment> findAllActiveAssignments();

    /**
     * Récupère les affectations futures (qui n'ont pas encore commencé)
     * 
     * @return List<Assignment> dont startDate > aujourd'hui
     */
    @Query("SELECT a FROM Assignment a WHERE a.startDate > CURRENT_DATE ORDER BY a.startDate")
    List<Assignment> findUpcomingAssignments();

    /**
     * Récupère les affectations terminées (dont la date de fin est passée)
     * Utile pour l'historique
     * 
     * @return List<Assignment> dont endDate < aujourd'hui
     */
    @Query("SELECT a FROM Assignment a WHERE a.endDate < CURRENT_DATE ORDER BY a.endDate DESC")
    List<Assignment> findCompletedAssignments();

    /**
     * Récupère les affectations dans une plage de dates
     * 
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return List<Assignment> dans cette plage
     */
    @Query("SELECT a FROM Assignment a WHERE a.startDate >= :startDate AND a.endDate <= :endDate")
    List<Assignment> findAssignmentsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Obtient toutes les affectations avec informations complètes (employee et project)
     * Requête optimisée avec JOIN FETCH pour éviter N+1 queries
     * 
     * @return List<Assignment> avec employee et project préchargés
     */
    @Query("SELECT DISTINCT a FROM Assignment a JOIN FETCH a.employee JOIN FETCH a.project")
    List<Assignment> findAllWithEmployeeAndProject();

    /**
     * Obtient une affectation avec toutes ses données (employee et project)
     * 
     * @param id l'ID de l'affectation
     * @return Optional<Assignment> complètement chargée
     */
    @Query("SELECT a FROM Assignment a JOIN FETCH a.employee JOIN FETCH a.project WHERE a.id = :id")
    Optional<Assignment> findByIdWithRelations(@Param("id") Long id);

    /**
     * Compte le nombre total d'affectations
     * 
     * @return le nombre total d'affectations
     */
    @Query("SELECT COUNT(a) FROM Assignment a")
    long countTotalAssignments();

    /**
     * Compte le nombre d'affectations actuellement actives
     * 
     * @return le nombre d'affectations actives
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE CURRENT_DATE BETWEEN a.startDate AND a.endDate")
    long countActiveAssignments();

    /**
     * Compte le nombre d'affectations pour un employé spécifique
     * 
     * @param employeeId l'ID de l'employé
     * @return le nombre total d'affectations
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.employee.id = :employeeId")
    long countAssignmentsByEmployee(@Param("employeeId") Long employeeId);

    /**
     * Compte le nombre d'affectations pour un projet spécifique
     * 
     * @param projectId l'ID du projet
     * @return le nombre total d'affectations
     */
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.project.id = :projectId")
    long countAssignmentsByProject(@Param("projectId") Long projectId);

    /**
     * Obtient les affectations d'un employé sur une plage de dates
     * 
     * @param employeeId l'ID de l'employé
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return List<Assignment> chevauchant la plage de dates
     */
    @Query("SELECT a FROM Assignment a WHERE a.employee.id = :employeeId " +
           "AND NOT (a.endDate < :startDate OR a.startDate > :endDate) " +
           "ORDER BY a.startDate")
    List<Assignment> findEmployeeAssignmentsInDateRange(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Obtient les affectations d'un projet sur une plage de dates
     * 
     * @param projectId l'ID du projet
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return List<Assignment> chevauchant la plage de dates
     */
    @Query("SELECT a FROM Assignment a WHERE a.project.id = :projectId " +
           "AND NOT (a.endDate < :startDate OR a.startDate > :endDate) " +
           "ORDER BY a.startDate")
    List<Assignment> findProjectAssignmentsInDateRange(
        @Param("projectId") Long projectId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Obtient les conflits d'affectations (même employé sur plusieurs projets à la même date)
     * Utile pour detecter les surcharges
     * 
     * @param employeeId l'ID de l'employé
     * @param startDate la date de début à vérifier
     * @param endDate la date de fin à vérifier
     * @return List<Assignment> qui chevauchent la période
     */
    @Query("SELECT a FROM Assignment a WHERE a.employee.id = :employeeId " +
           "AND NOT (a.endDate < :startDate OR a.startDate > :endDate)")
    List<Assignment> findConflictingAssignments(
        @Param("employeeId") Long employeeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    /**
     * Obtient les affectations triées par date de fin (ordre croissant)
     * Utile pour identifier les affectations qui se terminent bientôt
     * 
     * @return List<Assignment> triée par endDate
     */
    @Query("SELECT a FROM Assignment a ORDER BY a.endDate ASC")
    List<Assignment> findAllOrderedByEndDate();

    /**
     * Obtient les affectations qui se terminent dans N jours
     * Utile pour les notifications de fin d'affectation
     * 
     * @param daysFromNow le nombre de jours
     * @return List<Assignment> se terminant bientôt
     */
    @Query("SELECT a FROM Assignment a WHERE a.endDate >= CURRENT_DATE AND a.endDate <= CURRENT_DATE + :days")
    List<Assignment> findAssignmentsEndingInDays(@Param("days") int daysFromNow);

    /**
     * Supprime toutes les affectations d'un employé
     * Attention : utiliser avec prudence lors de la suppression d'un employé
     * 
     * @param employeeId l'ID de l'employé
     */
    @Query("DELETE FROM Assignment a WHERE a.employee.id = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Supprime toutes les affectations d'un projet
     * Attention : utiliser avec prudence lors de la suppression d'un projet
     * 
     * @param projectId l'ID du projet
     */
    @Query("DELETE FROM Assignment a WHERE a.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);
}
