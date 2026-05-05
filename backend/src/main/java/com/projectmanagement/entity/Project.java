package com.projectmanagement.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Project représentant un projet de l'organisation
 * 
 * Relations :
 * - OneToMany avec Assignment
 */
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_title", columnList = "title")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    /**
     * Identifiant unique du projet
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Titre du projet
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Description détaillée du projet
     */
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;

    /**
     * Date de début du projet
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Date de fin du projet
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Relation OneToMany avec Assignment
     * Un projet contient plusieurs affectations (employés)
     */
    @OneToMany(
        mappedBy = "project",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Assignment> assignments = new HashSet<>();

    /**
     * Ajoute une affectation au projet
     */
    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
        assignment.setProject(this);
    }

    /**
     * Supprime une affectation du projet
     */
    public void removeAssignment(Assignment assignment) {
        this.assignments.remove(assignment);
        assignment.setProject(null);
    }

    /**
     * Vérifie si le projet est actif (date actuelle entre startDate et endDate)
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(this.startDate) && !today.isAfter(this.endDate);
    }

    /**
     * Retourne le nombre d'employés affectés au projet
     */
    public int getEmployeeCount() {
        return this.assignments.size();
    }
}
