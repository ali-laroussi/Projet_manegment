package com.projectmanagement.entity;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Employee représentant un employé du système
 * 
 * Relations :
 * - ManyToOne avec Category
 * - OneToMany avec Assignment
 */
@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_category", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    /**
     * Identifiant unique de l'employé
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Prénom de l'employé
     */
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    /**
     * Nom de famille de l'employé
     */
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    /**
     * Email unique de l'employé utilisé pour l'authentification
     */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Mot de passe hashé (BCrypt)
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Rôle de l'utilisateur (ADMIN ou EMPLOYEE)
     * Valeur par défaut : EMPLOYEE
     */
    @Column(name = "role", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.EMPLOYEE;

    /**
     * Relation ManyToOne avec Category
     * Un employé appartient obligatoirement à une catégorie
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = 
        @ForeignKey(name = "fk_employee_category"))
    private Category category;

    /**
     * Relation OneToMany avec Assignment
     * Un employé peut avoir plusieurs affectations (projets)
     */
    @OneToMany(
        mappedBy = "employee",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Assignment> assignments = new HashSet<>();

    /**
     * Ajoute une affectation à cet employé
     */
    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        assignment.setEmployee(this);
    }

    /**
     * Supprime une affectation de cet employé
     */
    public void removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
        assignment.setEmployee(null);
    }

    /**
     * Retourne le nom complet de l'employé
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
