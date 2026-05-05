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
 * Service métier pour la gestion des projets.
 *
 * Cette classe encapsule la logique métier pour :
 * - CRUD sur les projets
 * - Affectation/désaffectation d'employés
 * - Filtrage par statut (actif, en attente, terminé)
 * - Statistiques et analytics
 * - Gestion des dates et calendrier des projets
 *
 * @author ProjectManagement
 * @version 1.0
 */
@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    // ===================== OPÉRATIONS CRUD =====================

    /**
     * Récupère tous les projets triés par date de début.
     *
     * @return liste de tous les projets
     */
    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return projectRepository.findAllOrderedByStartDate();
    }

    /**
     * Récupère tous les projets avec leurs relations chargées.
     *
     * @return liste de tous les projets avec affectations
     */
    @Transactional(readOnly = true)
    public List<Project> findAllWithRelations() {
        return projectRepository.findAllWithAssignments();
    }

    /**
     * Récupère un projet par son identifiant.
     *
     * @param id l'identifiant du projet
     * @return Optional contenant le projet si existant
     */
    @Transactional(readOnly = true)
    public Optional<Project> findById(Long id) {
        return projectRepository.findByIdWithAssignments(id);
    }

    /**
     * Crée un nouveau projet.
     *
     * Validations:
     * - Le titre ne peut pas être vide
     * - Le titre doit être unique
     * - La date de fin doit être après ou égale à la date de début
     *
     * @param project le projet à créer
     * @return le projet créé avec son ID généré
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Project save(Project project) {
        validateProject(project);

        if (projectRepository.existsByTitleIgnoreCase(project.getTitle())) {
            throw new IllegalArgumentException("Un projet avec ce titre existe déjà");
        }

        if (project.getEndDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return projectRepository.save(project);
    }

    /**
     * Met à jour un projet existant.
     *
     * Validations:
     * - Le projet doit exister
     * - Le nouveau titre ne peut pas être un doublon
     * - La date de fin doit être après la date de début
     *
     * @param project le projet avec données mises à jour
     * @return le projet mis à jour
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Project update(Project project) {
        if (project.getId() == null || !projectRepository.existsById(project.getId())) {
            throw new IllegalArgumentException("Le projet avec l'ID " + project.getId() + " n'existe pas");
        }

        validateProject(project);

        if (project.getEndDate().isBefore(project.getStartDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        return projectRepository.save(project);
    }

    /**
     * Supprime un projet par son identifiant.
     *
     * Note: Les affectations associées seront automatiquement supprimées
     * (relation avec orphanRemoval=true).
     *
     * @param id l'identifiant du projet
     * @throws IllegalArgumentException si le projet n'existe pas
     */
    public void delete(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + id + " n'existe pas");
        }
        projectRepository.deleteById(id);
    }

    // ===================== OPÉRATIONS SPÉCIALISÉES =====================

    /**
     * Récupère tous les projets actuellement actifs.
     *
     * Un projet est actif si la date actuelle est entre sa date de début et de fin.
     *
     * @return liste des projets actifs
     */
    @Transactional(readOnly = true)
    public List<Project> findActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    /**
     * Récupère tous les projets qui n'ont pas encore commencé.
     *
     * @return liste des projets futurs
     */
    @Transactional(readOnly = true)
    public List<Project> findUpcomingProjects() {
        return projectRepository.findUpcomingProjects();
    }

    /**
     * Récupère tous les projets qui sont terminés.
     *
     * @return liste des projets terminés
     */
    @Transactional(readOnly = true)
    public List<Project> findCompletedProjects() {
        return projectRepository.findCompletedProjects();
    }

    /**
     * Récupère les projets dans une plage de dates.
     *
     * @param startDate la date de début (incluse)
     * @param endDate la date de fin (incluse)
     * @return liste des projets dans cette plage
     */
    @Transactional(readOnly = true)
    public List<Project> findProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }
        return projectRepository.findProjectsByDateRange(startDate, endDate);
    }

    /**
     * Recherche des projets par titre.
     *
     * @param titleSearchTerm le terme de recherche
     * @return liste des projets correspondants
     */
    @Transactional(readOnly = true)
    public List<Project> searchByTitle(String titleSearchTerm) {
        if (titleSearchTerm == null || titleSearchTerm.trim().isEmpty()) {
            return findAll();
        }
        return projectRepository.findByTitleContainingIgnoreCase(titleSearchTerm.trim());
    }

    /**
     * Recherche des projets par description.
     *
     * @param descriptionSearchTerm le terme de recherche
     * @return liste des projets correspondants
     */
    @Transactional(readOnly = true)
    public List<Project> searchByDescription(String descriptionSearchTerm) {
        if (descriptionSearchTerm == null || descriptionSearchTerm.trim().isEmpty()) {
            return findAll();
        }
        return projectRepository.findByDescriptionContainingIgnoreCase(descriptionSearchTerm.trim());
    }

    /**
     * Récupère tous les projets assignés à un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return liste des projets de l'employé
     */
    @Transactional(readOnly = true)
    public List<Project> findByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return projectRepository.findByEmployeeId(employeeId);
    }

    /**
     * Assigne un employé à un projet.
     *
     * Validations:
     * - L'employé et le projet doivent exister
     * - L'employé ne doit pas déjà être assigné au projet
     * - Pas d'affectation déjà en cours pour cet employé sur cette période
     *
     * @param projectId l'identifiant du projet
     * @param employeeId l'identifiant de l'employé
     * @param startDate la date de début de l'affectation
     * @param endDate la date de fin de l'affectation
     * @return l'Assignment créé
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Assignment addEmployeeToProject(Long projectId, Long employeeId, LocalDate startDate, LocalDate endDate) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }

        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        if (assignmentRepository.existsByEmployeeIdAndProjectId(employeeId, projectId)) {
            throw new IllegalArgumentException("Cet employé est déjà assigné à ce projet");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        Project project = projectRepository.findById(projectId).orElseThrow();
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        Assignment assignment = new Assignment();
        assignment.setProject(project);
        assignment.setEmployee(employee);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);

        return assignmentRepository.save(assignment);
    }

    /**
     * Désaffecte un employé d'un projet.
     *
     * @param projectId l'identifiant du projet
     * @param employeeId l'identifiant de l'employé
     * @throws IllegalArgumentException si l'affectation n'existe pas
     */
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        Optional<Assignment> assignment = assignmentRepository.findByEmployeeIdAndProjectId(employeeId, projectId);

        if (assignment.isEmpty()) {
            throw new IllegalArgumentException("Cet employé n'est pas assigné à ce projet");
        }

        assignmentRepository.delete(assignment.get());
    }

    /**
     * Récupère les employés actuellement assignés à un projet.
     *
     * @param projectId l'identifiant du projet
     * @return liste des employés du projet
     */
    @Transactional(readOnly = true)
    public List<Employee> getProjectTeam(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return employeeRepository.findByProjectId(projectId);
    }

    // ===================== STATISTIQUES =====================

    /**
     * Compte le nombre total de projets.
     *
     * @return nombre total de projets
     */
    @Transactional(readOnly = true)
    public long countTotal() {
        return projectRepository.countTotalProjects();
    }

    /**
     * Compte le nombre de projets actuellement actifs.
     *
     * @return nombre de projets actifs
     */
    @Transactional(readOnly = true)
    public long countActiveProjects() {
        return projectRepository.countActiveProjects();
    }

    /**
     * Compte le nombre d'employés assignés à un projet.
     *
     * @param projectId l'identifiant du projet
     * @return nombre d'employés
     */
    @Transactional(readOnly = true)
    public long countEmployeesInProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return projectRepository.countEmployeesByProject(projectId);
    }

    /**
     * Compte le nombre d'affectations actuellement actives pour un projet.
     *
     * @param projectId l'identifiant du projet
     * @return nombre d'affectations actives
     */
    @Transactional(readOnly = true)
    public long countActiveAssignments(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Le projet avec l'ID " + projectId + " n'existe pas");
        }
        return projectRepository.countActiveAssignmentsByProject(projectId);
    }

    // ===================== MÉTHODES UTILITAIRES PRIVÉES =====================

    /**
     * Valide les données d'un projet.
     *
     * @param project le projet à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateProject(Project project) {
        if (project.getTitle() == null || project.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du projet ne peut pas être vide");
        }

        if (project.getStartDate() == null) {
            throw new IllegalArgumentException("La date de début est requise");
        }

        if (project.getEndDate() == null) {
            throw new IllegalArgumentException("La date de fin est requise");
        }
    }
}
