package com.projectmanagement.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entité Assignment représentant l'affectation d'un employé à un projet
 * 
 * Relations :
 * - ManyToOne avec Employee
 * - ManyToOne avec Project
 * 
 * Cette entité représente la relation many-to-many entre Employee et Project
 */
@Entity
@Table(name = "assignments", indexes = {
    @Index(name = "idx_employee", columnList = "employee_id"),
    @Index(name = "idx_project", columnList = "project_id"),
    @Index(name = "idx_employee_project", columnList = "employee_id,project_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    /**
     * Identifiant unique de l'affectation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Relation ManyToOne avec Employee
     * Une affectation est toujours liée à un employé
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = 
        @ForeignKey(name = "fk_assignment_employee"))
    private Employee employee;

    /**
     * Relation ManyToOne avec Project
     * Une affectation est toujours liée à un projet
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = 
        @ForeignKey(name = "fk_assignment_project"))
    private Project project;

    /**
     * Date de début de l'affectation
     * L'employé commence à travailler sur le projet à cette date
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * Date de fin de l'affectation
     * L'employé arrête de travailler sur le projet à cette date
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * Vérifie si l'affectation est actuellement active
     */
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(this.startDate) && !today.isAfter(this.endDate);
    }

    /**
     * Calcule la durée de l'affectation en jours
     */
    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(this.startDate, this.endDate);
    }

    /**
     * Retourne une description de l'affectation
     */
    public String getDescription() {
        return String.format("%s affecté à %s du %s au %s",
            employee.getFullName(),
            project.getTitle(),
            startDate,
            endDate);
    }
}
