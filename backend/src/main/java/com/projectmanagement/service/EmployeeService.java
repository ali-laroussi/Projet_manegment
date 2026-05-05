package com.projectmanagement.service;

import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.UserRole;
import com.projectmanagement.repository.EmployeeRepository;
import com.projectmanagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des employés et l'authentification.
 *
 * Cette classe encapsule la logique métier pour :
 * - CRUD sur les employés
 * - Authentification et validation des credentials
 * - Gestion des projets assignés aux employés
 * - Recherche et filtrage des employés
 * - Statistiques sur les employés
 *
 * @author ProjectManagement
 * @version 1.0
 */
@Service
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===================== OPÉRATIONS CRUD =====================

    /**
     * Récupère tous les employés triés par nom.
     *
     * @return liste de tous les employés
     */
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return employeeRepository.findAllOrderedByName();
    }

    /**
     * Récupère tous les employés avec leurs relations chargées.
     *
     * @return liste de tous les employés avec catégories et affectations
     */
    @Transactional(readOnly = true)
    public List<Employee> findAllWithRelations() {
        return employeeRepository.findAllWithAssignments();
    }

    /**
     * Récupère un employé par son identifiant.
     *
     * @param id l'identifiant de l'employé
     * @return Optional contenant l'employé si existant
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findByIdWithRelations(id);
    }

    /**
     * Récupère un employé par son email.
     *
     * Utilisé pour l'authentification.
     *
     * @param email l'email de l'employé
     * @return Optional contenant l'employé si existant
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    /**
     * Crée un nouvel employé.
     *
     * Validations:
     * - L'email ne peut pas être null ou vide
     * - L'email doit être unique
     * - Le prénom et nom ne peuvent pas être vides
     * - La catégorie doit exister
     * - Le mot de passe sera hashé automatiquement
     *
     * @param employee l'employé à créer
     * @return l'employé créé avec son ID généré
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Employee save(Employee employee) {
        validateEmployee(employee);

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Un employé avec cet email existe déjà");
        }

        // Hasher le mot de passe
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        // Définir le rôle par défaut si non spécifié
        if (employee.getRole() == null) {
            employee.setRole(UserRole.EMPLOYEE);
        }

        return employeeRepository.save(employee);
    }

    /**
     * Met à jour un employé existant.
     *
     * Validations:
     * - L'employé doit exister
     * - L'email ne peut pas devenir un doublon
     *
     * @param employee l'employé avec données mises à jour
     * @return l'employé mis à jour
     * @throws IllegalArgumentException si l'employé n'existe pas ou données invalides
     */
    public Employee update(Employee employee) {
        if (employee.getId() == null || !employeeRepository.existsById(employee.getId())) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employee.getId() + " n'existe pas");
        }

        validateEmployee(employee);

        // Vérifier l'unicité de l'email (sauf pour l'employé lui-même)
        if (employeeRepository.existsEmailExcluding(employee.getEmail(), employee.getId())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre employé");
        }

        return employeeRepository.save(employee);
    }

    /**
     * Supprime un employé par son identifiant.
     *
     * Note: Les affectations de cet employé seront automatiquement supprimées
     * (relation avec orphanRemoval=true).
     *
     * @param id l'identifiant de l'employé
     * @throws IllegalArgumentException si l'employé n'existe pas
     */
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + id + " n'existe pas");
        }
        employeeRepository.deleteById(id);
    }

    // ===================== AUTHENTIFICATION =====================

    /**
     * Authentifie un employé avec son email et mot de passe.
     *
     * @param email l'email de l'employé
     * @param rawPassword le mot de passe en clair
     * @return Optional contenant l'employé si authentification réussie
     */
    @Transactional(readOnly = true)
    public Optional<Employee> authenticate(String email, String rawPassword) {
        Optional<Employee> employeeOpt = findByEmail(email);

        if (employeeOpt.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeOpt.get();
        if (passwordEncoder.matches(rawPassword, employee.getPassword())) {
            return Optional.of(employee);
        }

        return Optional.empty();
    }

    /**
     * Change le mot de passe d'un employé.
     *
     * @param employeeId l'ID de l'employé
     * @param oldPassword l'ancien mot de passe
     * @param newPassword le nouveau mot de passe
     * @return true si le changement a réussi
     * @throws IllegalArgumentException si l'ancien mot de passe est incorrect
     */
    public boolean changePassword(Long employeeId, String oldPassword, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        Employee employee = employeeOpt.get();

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);

        return true;
    }

    // ===================== OPÉRATIONS SPÉCIALISÉES =====================

    /**
     * Récupère tous les projets assignés à un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return liste des projets de l'employé
     */
    @Transactional(readOnly = true)
    public List<Project> getProjectsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return projectRepository.findByEmployeeId(employeeId);
    }

    /**
     * Récupère les projets actuellement actifs d'un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return liste des projets actifs de l'employé
     */
    @Transactional(readOnly = true)
    public List<Project> getActiveProjectsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return projectRepository.findActiveProjectsByEmployeeId(employeeId);
    }

    /**
     * Compte le nombre de tâches actuellement actives pour un employé.
     *
     * @param employeeId l'identifiant de l'employé
     * @return nombre de tâches actives
     */
    @Transactional(readOnly = true)
    public long countActiveAssignments(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }
        return employeeRepository.countActiveAssignments(employeeId);
    }

    /**
     * Recherche les employés par nom complet.
     *
     * La recherche est case-insensitive et flexible.
     *
     * @param searchTerm le terme de recherche (prénom, nom ou combinaison)
     * @return liste des employés correspondant au critère
     */
    @Transactional(readOnly = true)
    public List<Employee> searchByFullName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return employeeRepository.searchByFullName(searchTerm.trim());
    }

    /**
     * Récupère tous les employés d'une catégorie.
     *
     * @param categoryId l'identifiant de la catégorie
     * @return liste des employés de cette catégorie
     */
    @Transactional(readOnly = true)
    public List<Employee> findByCategory(Long categoryId) {
        return employeeRepository.findByCategoryIdOrderByName(categoryId);
    }

    /**
     * Récupère tous les employés avec un rôle spécifique.
     *
     * @param role le rôle (ADMIN, EMPLOYEE)
     * @return liste des employés avec ce rôle
     */
    @Transactional(readOnly = true)
    public List<Employee> findByRole(UserRole role) {
        return employeeRepository.findByRole(role);
    }

    /**
     * Récupère tous les employés assignés à un projet.
     *
     * @param projectId l'identifiant du projet
     * @return liste des employés du projet
     */
    @Transactional(readOnly = true)
    public List<Employee> findByProject(Long projectId) {
        return employeeRepository.findByProjectId(projectId);
    }

    // ===================== STATISTIQUES =====================

    /**
     * Compte le nombre total d'employés.
     *
     * @return nombre total d'employés
     */
    @Transactional(readOnly = true)
    public long countTotal() {
        return employeeRepository.countTotalEmployees();
    }

    /**
     * Compte le nombre d'employés avec un rôle spécifique.
     *
     * @param role le rôle à compter
     * @return nombre d'employés avec ce rôle
     */
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return employeeRepository.countByRole(role);
    }

    /**
     * Vérifie si un email est unique (sauf pour un employé spécifique).
     *
     * @param email l'email à vérifier
     * @param excludeEmployeeId l'ID de l'employé à exclure (pour update)
     * @return true si l'email est unique
     */
    @Transactional(readOnly = true)
    public boolean isEmailUnique(String email, Long excludeEmployeeId) {
        return employeeRepository.existsEmailExcluding(email, excludeEmployeeId);
    }

    // ===================== MÉTHODES UTILITAIRES PRIVÉES =====================

    /**
     * Valide les données d'un employé.
     *
     * @param employee l'employé à valider
     * @throws IllegalArgumentException si les données sont invalides
     */
    private void validateEmployee(Employee employee) {
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }

        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }

        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        // Validation email basique
        if (!employee.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Le format de l'email est invalide");
        }

        if (employee.getCategory() == null || employee.getCategory().getId() == null) {
            throw new IllegalArgumentException("Une catégorie doit être assignée");
        }
    }
}
