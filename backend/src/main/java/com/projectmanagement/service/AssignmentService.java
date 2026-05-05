package com.projectmanagement.service;

import com.projectmanagement.entity.Assignment;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Project;
import com.projectmanagement.repository.AssignmentRepository;
import com.projectmanagement.repository.EmployeeRepository;
import com.projectmanagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des affectations (assignments).
 *
 * Cette classe encapsule la logique métier pour :
 * - CRUD sur les affectations employé-projet
 * - Gestion des calendriers et plages de dates
 * - Détection de surcharge et conflits d'affectations
 * - Filtrage par statut (actif, en attente, terminé)
 * - Statistiques sur les affectations
 *
 * @author ProjectManagement
 * @version 1.0
 */
@Service
@Transactional
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // ===================== OPÉRATIONS CRUD =====================

    /**
     * Récupère toutes les affectations triées par date de fin.
     *
     * @return liste de toutes les affectations
     */
    @Transactional(readOnly = true)
    public List<Assignment> findAll() {
        return assignmentRepository.findAllOrderedByEndDate();
    }

    /**
     * Récupère toutes les affectations avec leurs relations chargées.
     *
     * @return liste de toutes les affectations avec employé et projet
     */
    @Transactional(readOnly = true)
    public List<Assignment> findAllWithRelations() {
        return assignmentRepository.findAllWithEmployeeAndProject();
    }

    /**
     * Récupère une affectation par son identifiant.
     *
     * @param id l'identifiant de l'affectation
     * @return Optional contenant l'affectation si existante
     */
    @Transactional(readOnly = true)
    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findByIdWithRelations(id);
    }

    /**
     * Crée une nouvelle affectation.
     *
     * Validations:
     * - L'employé et le projet doivent exister
     * - Les dates doivent être valides
     * - Pas de doublon employé-projet
     *
     * @param assignment l'affectation à créer
     * @return l'affectation créée avec son ID généré
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Assignment save(Assignment assignment) {
        validateAssignment(assignment);

        if (assignmentRepository.existsByEmployeeIdAndProjectId(
                assignment.getEmployee().getId(),
                assignment.getProject().getId())) {
            throw new IllegalArgumentException("Cet employé est déjà assigné à ce projet");
        }

        if (assignment.getEndDate().isBefore(assignment.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return assignmentRepository.save(assignment);
    }

    /**
     * Met à jour une affectation existante.
     *
     * Validations:
     * - L'affectation doit exister
     * - Les données doivent être valides
     *
     * @param assignment l'affectation avec données mises à jour
     * @return l'affectation mise à jour
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Assignment update(Assignment assignment) {
        if (assignment.getId() == null || !assignmentRepository.existsById(assignment.getId())) {
            throw new IllegalArgumentException("L'affectation avec l'ID " + assignment.getId() + " n'existe pas");
        }

        validateAssignment(assignment);

        if (assignment.getEndDate().isBefore(assignment.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return assignmentRepository.save(assignment);
    }

    /**
     * Supprime une affectation par son identifiant.
     *
     * @param id l'identifiant de l'affectation
     * @throws IllegalArgumentException si l'affectation n'existe pas
     */
    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new IllegalArgumentException("L'affectation avec l'ID " + id + " n'existe pas");
        }
        assignmentRepository.deleteById(id);
    }

    // ===================== OPÉRATIONS SPÉCIALISÉES =====================

    /**
     * Récupère toutes les affectations d'un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return liste des affectations de l'employé
     */
    @Transactional(readOnly = true)
    public List<Assignment> findByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return assignmentRepository.findByEmployeeIdOrderByEndDate(employeeId);
    }

    /**
     * Récupère toutes les affectations actuellement actives d'un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return liste des affectations actuellement actives
     */
    @Transactional(readOnly = true)
    public List<Assignment> findActiveByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return assignmentRepository.findActiveAssignmentsByEmployeeId(employeeId);
    }

    /**
     * Récupère toutes les affectations d'un projet.
     *
     * @param projectId l'identifiant du projet
     * @return liste des affectations du projet
     */
    @Transactional(readOnly = true)
    public List<Assignment> findByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return assignmentRepository.findByProjectIdOrderByEmployee(projectId);
    }

    /**
     * Récupère toutes les affectations actuellement actives d'un projet.
     *
     * @param projectId l'identifiant du projet
     * @return liste des affectations actuellement actives
     */
    @Transactional(readOnly = true)
    public List<Assignment> findActiveByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return assignmentRepository.findActiveAssignmentsByProjectId(projectId);
    }

    /**
     * Récupère une affectation spécifique entre un employé et un projet.
     *
     * @param employeeId l'identifiant de l'employé
     * @param projectId l'identifiant du projet
     * @return Optional contenant l'affectation si elle existe
     */
    @Transactional(readOnly = true)
    public Optional<Assignment> findByEmployeeAndProject(Long employeeId, Long projectId) {
        return assignmentRepository.findByEmployeeIdAndProjectId(employeeId, projectId);
    }

    /**
     * Récupère toutes les affectations actuellement actives.
     *
     * @return liste de toutes les affectations en cours
     */
    @Transactional(readOnly = true)
    public List<Assignment> findAllActive() {
        return assignmentRepository.findAllActiveAssignments();
    }

    /**
     * Récupère toutes les affectations futures (pas encore commencées).
     *
     * @return liste des affectations futures
     */
    @Transactional(readOnly = true)
    public List<Assignment> findUpcoming() {
        return assignmentRepository.findUpcomingAssignments();
    }

    /**
     * Récupère toutes les affectations terminées.
     *
     * @return liste des affectations complétées
     */
    @Transactional(readOnly = true)
    public List<Assignment> findCompleted() {
        return assignmentRepository.findCompletedAssignments();
    }

    /**
     * Récupère les affectations dans une plage de dates.
     *
     * @param startDate la date de début (incluse)
     * @param endDate la date de fin (incluse)
     * @return liste des affectations dans cette plage
     */
    @Transactional(readOnly = true)
    public List<Assignment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        return assignmentRepository.findAssignmentsByDateRange(startDate, endDate);
    }

    /**
     * Récupère les affectations d'un employé dans une plage de dates.
     *
     * @param employeeId l'identifiant de l'employé
     * @param startDate la date de début (incluse)
     * @param endDate la date de fin (incluse)
     * @return liste des affectations de l'employé dans cette plage
     */
    @Transactional(readOnly = true)
    public List<Assignment> findEmployeeAssignmentsByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return assignmentRepository.findEmployeeAssignmentsInDateRange(employeeId, startDate, endDate);
    }

    /**
     * Récupère les affectations d'un projet dans une plage de dates.
     *
     * @param projectId l'identifiant du projet
     * @param startDate la date de début (incluse)
     * @param endDate la date de fin (incluse)
     * @return liste des affectations du projet dans cette plage
     */
    @Transactional(readOnly = true)
    public List<Assignment> findProjectAssignmentsByDateRange(Long projectId, LocalDate startDate, LocalDate endDate) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return assignmentRepository.findProjectAssignmentsInDateRange(projectId, startDate, endDate);
    }

    /**
     * Récupère les affectations qui se terminent dans un certain nombre de jours.
     *
     * Utile pour les rappels et notifications.
     *
     * @param daysFromNow le nombre de jours (ex: 7 pour les affectations finissant dans 7 jours)
     * @return liste des affectations qui se terminent bientôt
     */
    @Transactional(readOnly = true)
    public List<Assignment> findAssignmentsEndingIn(int daysFromNow) {
        if (daysFromNow < 0) {
            daysFromNow = 0;
        }
        return assignmentRepository.findAssignmentsEndingInDays(daysFromNow);
    }

    // ===================== DÉTECTION DE CONFLITS =====================

    /**
     * Détecte les affectations en conflit pour un employé sur une plage de dates.
     *
     * Un conflit existe si un employé est assigné à plusieurs projets qui se chevauchent.
     *
     * @param employeeId l'identifiant de l'employé
     * @param startDate la date de début de la période à vérifier
     * @param endDate la date de fin de la période à vérifier
     * @return liste des affectations en conflit
     */
    @Transactional(readOnly = true)
    public List<Assignment> detectConflicts(Long employeeId, LocalDate startDate, LocalDate endDate) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return assignmentRepository.findConflictingAssignments(employeeId, startDate, endDate);
    }

    /**
     * Vérifie si un employé a des affectations en conflit pour une plage de dates.
     *
     * @param employeeId l'identifiant de l'employé
     * @param startDate la date de début
     * @param endDate la date de fin
     * @return true si des conflits sont détectés
     */
    @Transactional(readOnly = true)
    public boolean hasConflicts(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return !detectConflicts(employeeId, startDate, endDate).isEmpty();
    }

    // ===================== STATISTIQUES =====================

    /**
     * Compte le nombre total d'affectations.
     *
     * @return nombre total d'affectations
     */
    @Transactional(readOnly = true)
    public long countTotal() {
        return assignmentRepository.countTotalAssignments();
    }

    /**
     * Compte le nombre d'affectations actuellement actives.
     *
     * @return nombre d'affectations actives
     */
    @Transactional(readOnly = true)
    public long countActive() {
        return assignmentRepository.countActiveAssignments();
    }

    /**
     * Compte le nombre d'affectations pour un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return nombre d'affectations
     */
    @Transactional(readOnly = true)
    public long countByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return assignmentRepository.countAssignmentsByEmployee(employeeId);
    }

    /**
     * Compte le nombre d'affectations pour un projet.
     *
     * @param projectId l'identifiant du projet
     * @return nombre d'affectations
     */
    @Transactional(readOnly = true)
    public long countByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return assignmentRepository.countAssignmentsByProject(projectId);
    }

    // ===================== MÉTHODES UTILITAIRES PRIVÉES =====================

    /**
     * Valide les données d'une affectation.
     *
     * @param assignment l'affectation à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateAssignment(Assignment assignment) {
        if (assignment.getEmployee() == null || assignment.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Un employé doit être assigné");
        }

        if (assignment.getProject() == null || assignment.getProject().getId() == null) {
            throw new IllegalArgumentException("Un projet doit être assigné");
        }

        if (assignment.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est requise");
        }

        if (assignment.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est requise");
        }

        if (!employeeRepository.existsById(assignment.getEmployee().getId())) {
            throw new IllegalArgumentException("L'employé avec l'ID " + assignment.getEmployee().getId() + " n'existe pas");
        }

        if (!projectRepository.existsById(assignment.getProject().getId())) {
            throw new IllegalArgumentException("Le projet avec l'ID " + assignment.getProject().getId() + " n'existe pas");
        }
    }
}
