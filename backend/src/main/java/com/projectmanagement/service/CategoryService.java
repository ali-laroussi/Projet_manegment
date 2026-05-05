package com.projectmanagement.service;

import com.projectmanagement.entity.Category;
import com.projectmanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des catégories d'employés.
 *
 * Cette classe encapsule la logique métier pour :
 * - Créer, lire, mettre à jour, supprimer les catégories
 * - Rechercher et filtrer les catégories
 * - Fournir des statistiques sur les catégories
 *
 * @author ProjectManagement
 * @version 1.0
 */
@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // ===================== OPÉRATIONS CRUD =====================

    /**
     * Récupère toutes les catégories triées par nom.
     *
     * @return Liste de toutes les catégories
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAllOrderedByName();
    }

    /**
     * Récupère une catégorie par son identifiant.
     *
     * @param id l'identifiant de la catégorie
     * @return Optional contenant la catégorie si elle existe
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Récupère une catégorie par son nom.
     *
     * @param name le nom de la catégorie (ex: "Developer", "Manager")
     * @return Optional contenant la catégorie si elle existe
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * Crée une nouvelle catégorie.
     *
     * Validation:
     * - Le nom ne peut pas être null ou vide
     * - Le nom ne peut pas être un doublon (case-insensitive)
     *
     * @param category l'objet Category à créer
     * @return la catégorie créée avec son ID généré
     * @throws IllegalArgumentException si le nom est vide ou déjà utilisé
     */
    public Category save(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }

        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }

        return categoryRepository.save(category);
    }

    /**
     * Met à jour une catégorie existante.
     *
     * Validation:
     * - La catégorie doit exister
     * - Le nouveau nom ne peut pas être un doublon (sauf si c'est le même)
     *
     * @param category la catégorie avec les données mises à jour
     * @return la catégorie mise à jour
     * @throws IllegalArgumentException si la catégorie n'existe pas
     * @throws IllegalArgumentException si le nouveau nom est un doublon
     */
    public Category update(Category category) {
        if (category.getId() == null || !categoryRepository.existsById(category.getId())) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + category.getId() + " n'existe pas");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie ne peut pas être vide");
        }

        // Vérifier que le nouveau nom n'est pas un doublon (sauf si c'est le même)
        Optional<Category> existing = categoryRepository.findByName(category.getName());
        if (existing.isPresent() && !existing.get().getId().equals(category.getId())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà");
        }

        return categoryRepository.save(category);
    }

    /**
     * Supprime une catégorie par son identifiant.
     *
     * Note: Les employés associés à cette catégorie ne seront pas supprimés
     * (relation gérée par orphanRemoval=true dans l'entité).
     *
     * @param id l'identifiant de la catégorie à supprimer
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + id + " n'existe pas");
        }
        categoryRepository.deleteById(id);
    }

    // ===================== OPÉRATIONS SPÉCIALISÉES =====================

    /**
     * Recherche des catégories par mot-clé dans le nom.
     *
     * La recherche est case-insensitive.
     *
     * @param searchTerm le terme de recherche
     * @return liste des catégories correspondantes
     */
    @Transactional(readOnly = true)
    public List<Category> searchByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return categoryRepository.findByNameContainingIgnoreCase(searchTerm.trim());
    }

    /**
     * Récupère les catégories ayant au moins un nombre minimum d'employés.
     *
     * @param minEmployeeCount le nombre minimum d'employés
     * @return liste des catégories avec suffisamment d'employés
     */
    @Transactional(readOnly = true)
    public List<Category> findCategoriesWithMinEmployees(int minEmployeeCount) {
        if (minEmployeeCount < 0) {
            minEmployeeCount = 0;
        }
        return categoryRepository.findCategoriesWithMinEmployees(minEmployeeCount);
    }

    // ===================== STATISTIQUES =====================

    /**
     * Compte le nombre total de catégories.
     *
     * @return nombre total de catégories
     */
    @Transactional(readOnly = true)
    public long countAll() {
        return categoryRepository.countAllCategories();
    }

    /**
     * Compte le nombre d'employés dans une catégorie.
     *
     * @param categoryId l'identifiant de la catégorie
     * @return nombre d'employés dans la catégorie
     * @throws IllegalArgumentException si la catégorie n'existe pas
     */
    @Transactional(readOnly = true)
    public long countEmployees(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("La catégorie avec l'ID " + categoryId + " n'existe pas");
        }
        return categoryRepository.countEmployeesByCategory(categoryId);
    }

    /**
     * Vérifie si une catégorie existe par son nom.
     *
     * @param name le nom à vérifier
     * @return true si la catégorie existe, false sinon
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
}
