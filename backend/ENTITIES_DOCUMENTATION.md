# Documentation des Entités JPA

## Vue d'ensemble

Ce projet implémente une application de gestion de projets avec les 5 entités principales suivantes.

## Entités

### 1. Category
**Fichier** : `entity/Category.java`
- Représente une catégorie d'employés (ex: Developer, Manager, Designer)
- Champs:
  - `id` : Identifiant unique (clé primaire)
  - `name` : Nom de la catégorie (unique)
- Relations:
  - OneToMany avec Employee (une catégorie peut avoir plusieurs employés)

### 2. UserRole (Enum)
**Fichier** : `entity/UserRole.java`
- Énumération des rôles disponibles
- Valeurs:
  - `ADMIN` : Administrateur système
  - `EMPLOYEE` : Employé standard

### 3. Employee
**Fichier** : `entity/Employee.java`
- Représente un employé du système
- Champs:
  - `id` : Identifiant unique
  - `firstName` : Prénom
  - `lastName` : Nom de famille
  - `email` : Email unique (utilisé pour la connexion)
  - `password` : Mot de passe hashé (BCrypt)
  - `role` : Rôle (ADMIN ou EMPLOYEE)
  - `category` : Référence à la catégorie
- Relations:
  - ManyToOne avec Category
  - OneToMany avec Assignment

### 4. Project
**Fichier** : `entity/Project.java`
- Représente un projet de l'organisation
- Champs:
  - `id` : Identifiant unique
  - `title` : Titre du projet
  - `description` : Description longue
  - `startDate` : Date de début (LocalDate)
  - `endDate` : Date de fin (LocalDate)
- Relations:
  - OneToMany avec Assignment

### 5. Assignment
**Fichier** : `entity/Assignment.java`
- Représente l'affectation d'un employé à un projet (relation many-to-many)
- Champs:
  - `id` : Identifiant unique
  - `employee` : Référence à l'employé
  - `project` : Référence au projet
  - `startDate` : Date de début de l'affectation
  - `endDate` : Date de fin de l'affectation
- Relations:
  - ManyToOne avec Employee
  - ManyToOne avec Project

## Diagramme des Relations

```
Category
  |
  | OneToMany (cascade)
  |
  ├── Employee
  |     |
  |     | OneToMany (cascade)
  |     |
  |     └── Assignment
  |           |
  |           | ManyToOne
  |           |
  |           └── Project
  |                 |
  |                 | OneToMany (cascade)
  |                 |
  |                 └── Assignment
```

## Annotations JPA Utilisées

### @Entity
- Déclare que la classe est une entité JPA

### @Table
- Configure le nom de la table en base de données
- Peut contenir des indexes pour optimiser les requêtes

### @Id & @GeneratedValue
- @Id : Désigne le champ comme clé primaire
- @GeneratedValue(strategy = GenerationType.IDENTITY) : Auto-incrémentation de la clé

### @Column
- Configure les propriétés du champ en base de données
- Paramètres courants:
  - `nullable` : Si le champ peut être NULL
  - `unique` : Si le champ doit être unique
  - `length` : Longueur maximale (pour les String)
  - `columnDefinition` : Type SQL personnalisé

### @Enumerated
- Mappe une énumération Java à la base de données
- EnumType.STRING : Stocke la chaîne de caractères
- EnumType.ORDINAL : Stocke l'ordinal (0, 1, 2...)

### @OneToMany & @ManyToOne
- Défissent les relations entre entités

### @JoinColumn & @ForeignKey
- Configurent les clés étrangères en base de données

### @FetchType
- EAGER : Charge immédiatement les données associées
- LAZY : Charge les données à la demande

### @CascadeType
- ALL : Propager toutes les opérations (PERSIST, MERGE, REMOVE, REFRESH)
- REMOVE : Supprimer les enfants si le parent est supprimé

### @orphanRemoval
- true : Supprime les enfants qui ne sont plus associés au parent

## Annotations Lombok

### @Getter & @Setter
- Génère automatiquement les getters et setters

### @NoArgsConstructor
- Génère un constructeur sans paramètres

### @AllArgsConstructor
- Génère un constructeur avec tous les paramètres

### @Builder
- Génère le pattern Builder pour la création d'objets

### @Builder.Default
- Définit une valeur par défaut pour un champ lors de l'utilisation du Builder

## Configuration Base de Données

La configuration se trouve dans `application.properties`:
- **Driver** : MySQL 8 (com.mysql.cj.jdbc.Driver)
- **URL** : jdbc:mysql://localhost:3306/project_management_db
- **Utilisateur** : root
- **Mot de passe** : root

### Exécution du script SQL initial

```sql
CREATE DATABASE IF NOT EXISTS project_management_db 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE project_management_db;
-- Les tables seront créées automatiquement par Hibernate
```

## Prochaines étapes

1. **Repositories** : Créer les interfaces Spring Data JPA
2. **Services** : Implémentation de la logique métier
3. **Controllers** : Exposer les endpoints REST
4. **Security** : Configuration JWT et authentification
5. **DTOs** : Objets de transfert de données
