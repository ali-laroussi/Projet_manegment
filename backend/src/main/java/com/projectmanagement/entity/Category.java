package com.projectmanagement.entity;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité Category représentant une catégorie d'employés
 * 
 * Relations :
 * - OneToMany avec Employee
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    /**
     * Identifiant unique de la catégorie
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nom de la catégorie (ex: Developer, Manager, Designer, etc.)
     * Champ obligatoire et unique
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Relation OneToMany avec Employee
     * Un catégorie peut avoir plusieurs employés
     * Les employés sont supprimés en cascade si la catégorie est supprimée
     */
    @OneToMany(
        mappedBy = "category",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    /**
     * Ajoute un employé à cette catégorie
     */
    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setCategory(this);
    }

    /**
     * Supprime un employé de cette catégorie
     */
    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setCategory(null);
    }
}
