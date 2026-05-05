package com.projectmanagement.service;

import com.projectmanagement.entity.Category;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.UserRole;
import com.projectmanagement.repository.CategoryRepository;
import com.projectmanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service métier pour l'authentification et l'enregistrement d'employés.
 *
 * Cette classe encapsule la logique métier pour :
 * - Enregistrement de nouveaux employés
 * - Authentification des employés
 * - Validation des credentials
 * - Gestion des tokens JWT (sera intégré avec le JwtTokenProvider)
 *
 * @author ProjectManagement
 * @version 1.0
 */
@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===================== ENREGISTREMENT =====================

    /**
     * Enregistre un nouvel employé dans le système.
     *
     * Validations:
     * - L'email ne peut pas être un doublon
     * - Le prénom et nom ne peuvent pas être vides
     * - Une catégorie doit être assignée
     * - Le mot de passe sera hashé avec BCrypt
     *
     * @param employee l'employé à enregistrer
     * @param categoryId l'ID de la catégorie
     * @return l'employé enregistré
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Employee register(Employee employee, Long categoryId) {
        if (employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Un employé avec cet email existe déjà");
        }

        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être vide");
        }

        if (employee.getLastName() == null || employee.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }

        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Un mot de passe est requis");
        }

        // Valider l'email
        if (!employee.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Le format de l'email est invalide");
        }

        // Récupérer la catégorie
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + categoryId + " n'existe pas");
        }

        // Configuration de l'employé
        employee.setCategory(category.get());
        employee.setRole(UserRole.EMPLOYEE); // Nouveau rôle par défaut
        employee.setPassword(passwordEncoder.encode(employee.getPassword())); // Hash du mot de passe

        return employeeRepository.save(employee);
    }

    /**
     * Enregistre un nouvel employé admin.
     *
     * Similaire à register() mais avec le rôle ADMIN.
     *
     * @param employee l'employé admin à enregistrer
     * @param categoryId l'ID de la catégorie
     * @return l'employé admin enregistré
     * @throws IllegalArgumentException si les données sont invalides
     */
    public Employee registerAdmin(Employee employee, Long categoryId) {
        Employee registered = register(employee, categoryId);
        registered.setRole(UserRole.ADMIN);
        return employeeRepository.save(registered);
    }

    // ===================== AUTHENTIFICATION =====================

    /**
     * Authentifie un employé avec ses credentials.
     *
     * Effectue une recherche par email et valide le mot de passe.
     *
     * @param email l'email de l'employé
     * @param rawPassword le mot de passe en clair
     * @return Optional contenant l'employé si authentification réussie, vide sinon
     */
    @Transactional(readOnly = true)
    public Optional<Employee> authenticate(String email, String rawPassword) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        if (rawPassword == null || rawPassword.isEmpty()) {
            return Optional.empty();
        }

        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isEmpty()) {
            return Optional.empty();
        }

        Employee employee = employeeOpt.get();

        // Comparer les mots de passe
        if (passwordEncoder.matches(rawPassword, employee.getPassword())) {
            return Optional.of(employee);
        }

        return Optional.empty();
    }

    /**
     * Valide les credentials d'un employé sans retourner l'employé.
     *
     * Utile pour les vérifications sans exposer les données.
     *
     * @param email l'email de l'employé
     * @param rawPassword le mot de passe en clair
     * @return true si les credentials sont valides
     */
    @Transactional(readOnly = true)
    public boolean validateCredentials(String email, String rawPassword) {
        return authenticate(email, rawPassword).isPresent();
    }

    /**
     * Récupère un employé authentifié par son email.
     *
     * @param email l'email de l'employé
     * @return Optional contenant l'employé si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findAuthenticatedUser(String email) {
        return employeeRepository.findByEmail(email);
    }

    /**
     * Récupère un employé authentifié par son ID.
     *
     * @param employeeId l'ID de l'employé
     * @return Optional contenant l'employé si trouvé
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findAuthenticatedUserById(Long employeeId) {
        return employeeRepository.findById(employeeId);
    }

    // ===================== VALIDATION EMAIL =====================

    /**
     * Vérifie si un email existe dans le système.
     *
     * @param email l'email à vérifier
     * @return true si l'email existe
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    /**
     * Vérifie si un email est valide au format RFC.
     *
     * @param email l'email à valider
     * @return true si le format est valide
     */
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    // ===================== CHANGEMENT DE MOT DE PASSE =====================

    /**
     * Change le mot de passe d'un employé.
     *
     * Validations:
     * - L'ancien mot de passe doit être correct
     * - Le nouveau mot de passe ne peut pas être vide
     *
     * @param employeeId l'ID de l'employé
     * @param oldPassword l'ancien mot de passe en clair
     * @param newPassword le nouveau mot de passe en clair
     * @return true si le changement a réussi
     * @throws IllegalArgumentException si l'ancien mot de passe est incorrect
     */
    public boolean changePassword(Long employeeId, String oldPassword, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        Employee employee = employeeOpt.get();

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new IllegalArgumentException("L'ancien mot de passe est incorrect");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        // Mettre à jour le mot de passe
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);

        return true;
    }

    /**
     * Réinitialise le mot de passe d'un employé (administration).
     *
     * Utilisé par les administrateurs pour réinitialiser un mot de passe oublié.
     *
     * @param employeeId l'ID de l'employé
     * @param newPassword le nouveau mot de passe temporaire
     * @throws IllegalArgumentException si l'employé n'existe pas
     */
    public void resetPassword(Long employeeId, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("L'employé avec l'ID " + employeeId + " n'existe pas");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        Employee employee = employeeOpt.get();
        employee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(employee);
    }

    // ===================== VÉRIFICATIONS =====================

    /**
     * Vérifie si un employé est administrateur.
     *
     * @param employeeId l'ID de l'employé
     * @return true si l'employé a le rôle ADMIN
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.isPresent() && employee.get().getRole() == UserRole.ADMIN;
    }

    /**
     * Récupère le rôle d'un employé.
     *
     * @param employeeId l'ID de l'employé
     * @return le rôle de l'employé, ou null si non trouvé
     */
    @Transactional(readOnly = true)
    public UserRole getUserRole(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.map(Employee::getRole).orElse(null);
    }

    /**
     * Compte le nombre d'administrateurs dans le système.
     *
     * @return nombre d'administrateurs
     */
    @Transactional(readOnly = true)
    public long countAdmins() {
        return employeeRepository.countByRole(UserRole.ADMIN);
    }

    /**
     * Compte le nombre d'employés normaux dans le système.
     *
     * @return nombre d'employés
     */
    @Transactional(readOnly = true)
    public long countEmployees() {
        return employeeRepository.countByRole(UserRole.EMPLOYEE);
    }
}
