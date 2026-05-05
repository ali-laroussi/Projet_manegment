# 📚 Repositories Spring Data JPA - Phase 2 ✅

## 🎯 Vue d'ensemble

Quatre repositories Spring Data JPA ont été créés pour accéder et manipuler les données des entités :
- **CategoryRepository**
- **EmployeeRepository**
- **ProjectRepository**
- **AssignmentRepository**

Chaque repository étend `JpaRepository<Entity, Long>` et inclut des requêtes personnalisées optimisées.

---

## 📦 1. CategoryRepository

**Localisation** : `backend/src/main/java/com/projectmanagement/repository/CategoryRepository.java`

### Méthodes CRUD Héritées
- `save(Category)` - Créer/modifier
- `findById(Long)` - Trouver par ID
- `findAll()` - Lister tout
- `delete(Category)` - Supprimer
- `deleteById(Long)` - Supprimer par ID

### Méthodes Personnalisées (6)

| Méthode | Description | Utilité |
|---------|-------------|---------|
| `findByName(String)` | Trouver par nom unique | Login, validation unicité |
| `existsByName(String)` | Vérifier existence du nom | Validation avant création |
| `findByNameContainingIgnoreCase(String)` | Recherche flexible | Chercher catégories |
| `countAllCategories()` | Nombre total | Statistiques |
| `findAllOrderedByName()` | Lister triée | Affichage UI |
| `findCategoriesWithMinEmployees(int)` | Catégories actives | Analytics |
| `countEmployeesByCategory(Long)` | Employés par catégorie | Dashboard |

### Exemple d'Utilisation
```java
// Chercher une catégorie
Optional<Category> category = categoryRepository.findByName("Developer");

// Compter les employés
long employeeCount = categoryRepository.countEmployeesByCategory(1L);

// Lister toutes les catégories triées
List<Category> categories = categoryRepository.findAllOrderedByName();
```

---

## 👤 2. EmployeeRepository

**Localisation** : `backend/src/main/java/com/projectmanagement/repository/EmployeeRepository.java`

### Méthodes CRUD Héritées
- Mêmes méthodes que CategoryRepository

### Méthodes Personnalisées (16)

| Méthode | Description |
|---------|-------------|
| `findByEmail(String)` | Trouver par email (authentication) |
| `existsByEmail(String)` | Vérifier email unique |
| `findByCategoryId(Long)` | Employés d'une catégorie |
| `findByCategoryIdOrderByName(Long)` | Avec tri |
| `findByRole(UserRole)` | Employés par rôle (ADMIN/EMPLOYEE) |
| `countByRole(UserRole)` | Nombre par rôle |
| `searchByFullName(String)` | Recherche flexible |
| `findAllWithCategory()` | Avec joins optimisés |
| `findAllWithAssignments()` | Avec affectations préchargées |
| `findByIdWithRelations(Long)` | Vue détaillée complète |
| `findByProjectId(Long)` | Employés d'un projet |
| `findByCategoryAndProject(Long, Long)` | Filtre combiné |
| `countTotalEmployees()` | Total employés |
| `findAllOrderedByName()` | Listing trié |
| `countActiveAssignments(Long)` | Charge de travail |
| `existsEmailExcluding(String, Long)` | Validation modification email |

### Exemple d'Utilisation
```java
// Authentication
Optional<Employee> emp = employeeRepository.findByEmail("john@company.com");

// Voir les employés d'une catégorie
List<Employee> developers = employeeRepository.findByCategoryIdOrderByName(1L);

// Employés actuellement actifs sur les projets
long activeCount = employeeRepository.countActiveAssignments(1L);

// Employés d'un projet spécifique
List<Employee> projectTeam = employeeRepository.findByProjectId(2L);
```

---

## 📁 3. ProjectRepository

**Localisation** : `backend/src/main/java/com/projectmanagement/repository/ProjectRepository.java`

### Méthodes Personnalisées (19)

| Méthode | Description |
|---------|-------------|
| `findByTitleContainingIgnoreCase(String)` | Recherche titre |
| `findByDescriptionContainingIgnoreCase(String)` | Recherche description |
| `findByStartDateBefore(LocalDate)` | Projets commencés |
| `findByEndDateAfter(LocalDate)` | Projets futurs/en cours |
| `findActiveProjects()` | En cours aujourd'hui |
| `findUpcomingProjects()` | Non encore commencés |
| `findCompletedProjects()` | Terminés |
| `findProjectsByDateRange(LocalDate, LocalDate)` | Filtre période |
| `findAllWithAssignments()` | Avec affectations préchargées |
| `findByIdWithAssignments(Long)` | Vue détaillée |
| `countEmployeesByProject(Long)` | Effectif du projet |
| `countActiveAssignmentsByProject(Long)` | Affectations actives |
| `findByEmployeeId(Long)` | Projets d'un employé |
| `findActiveProjectsByEmployeeId(Long)` | Ses projets actuels |
| `findAllOrderedByStartDate()` | Listing chronologique |
| `countTotalProjects()` | Total projets |
| `countActiveProjects()` | Projets actuellement actifs |
| `findProjectsWithMinDuration(int)` | Filtre par durée |
| `existsByTitleIgnoreCase(String)` | Validation unicité titre |

### Exemple d'Utilisation
```java
// Projets actuellement actifs
List<Project> active = projectRepository.findActiveProjects();

// Projets futurs
List<Project> upcoming = projectRepository.findUpcomingProjects();

// Projets d'un employé
List<Project> empProjects = projectRepository.findByEmployeeId(1L);

// Projets par période
List<Project> quarterly = projectRepository.findProjectsByDateRange(
    LocalDate.of(2024, 1, 1),
    LocalDate.of(2024, 3, 31)
);
```

---

## 📌 4. AssignmentRepository

**Localisation** : `backend/src/main/java/com/projectmanagement/repository/AssignmentRepository.java`

### Méthodes Personnalisées (22)

| Méthode | Description |
|---------|-------------|
| `findByEmployeeId(Long)` | Affectations d'un employé |
| `findByEmployeeIdOrderByEndDate(Long)` | Avec tri date |
| `findByProjectId(Long)` | Affectations d'un projet |
| `findByProjectIdOrderByEmployee(Long)` | Employés du projet |
| `findByEmployeeIdAndProjectId(Long, Long)` | Affectation spécifique |
| `existsByEmployeeIdAndProjectId(Long, Long)` | Vérifier existence |
| `findActiveAssignmentsByEmployeeId(Long)` | Tâches actuelles |
| `findActiveAssignmentsByProjectId(Long)` | Affectations actuelles |
| `findAllActiveAssignments()` | Tout ce qui est en cours |
| `findUpcomingAssignments()` | Futures affectations |
| `findCompletedAssignments()` | Affectations terminées |
| `findAssignmentsByDateRange(LocalDate, LocalDate)` | Filtre période |
| `findAllWithEmployeeAndProject()` | Données complètes |
| `findByIdWithRelations(Long)` | Vue détaillée |
| `countTotalAssignments()` | Total global |
| `countActiveAssignments()` | Actuellement actives |
| `countAssignmentsByEmployee(Long)` | Par employé |
| `countAssignmentsByProject(Long)` | Par projet |
| `findEmployeeAssignmentsInDateRange(Long, LocalDate, LocalDate)` | Calendrier employé |
| `findProjectAssignmentsInDateRange(Long, LocalDate, LocalDate)` | Calendrier projet |
| `findConflictingAssignments(Long, LocalDate, LocalDate)` | Détect surcharge |
| `findAllOrderedByEndDate()` | Listing chronologique |
| `findAssignmentsEndingInDays(int)` | Fins proches |
| `deleteByEmployeeId(Long)` | Nettoyage en cascade |
| `deleteByProjectId(Long)` | Nettoyage en cascade |

### Exemple d'Utilisation
```java
// Affectations actuelles d'un employé
List<Assignment> current = assignmentRepository.findActiveAssignmentsByEmployeeId(1L);

// Projets auxquels il est affecté aujourd'hui
List<Assignment> today = assignmentRepository.findAllActiveAssignments();

// Détecter les surcharges
List<Assignment> conflicts = assignmentRepository.findConflictingAssignments(
    1L, 
    LocalDate.now(), 
    LocalDate.now().plusDays(30)
);

// Affectations qui se terminent dans 7 jours
List<Assignment> ending = assignmentRepository.findAssignmentsEndingInDays(7);
```

---

## 🔍 Concepts Importants

### 1. **Spring Data JPA**
- Étend `JpaRepository<Entity, Long>`
- Fournit CRUD automatique
- Support des méthodes nommées (findBy...)

### 2. **Requêtes Personnalisées**
- Utilisation de `@Query` pour requêtes complexes
- JPQL (Java Persistence Query Language)
- Support des `@Param` pour les paramètres

### 3. **Optimisations**
- `JOIN FETCH` pour éviter N+1 queries
- `DISTINCT` pour éviter les doublons
- Indexes pour la performance

### 4. **Paramètres**
- **@Param("name")** : Nommage explicite des paramètres
- Support des LocalDate, LocalDateTime, enums
- Conversion automatique

---

## 📊 Comparaison des Repositories

| Repository | Méthodes | Requêtes Complexes | Cas d'Usage |
|------------|----------|-------------------|-----------|
| **Category** | 7 | 4 | Gestion catégories |
| **Employee** | 16 | 8 | Authentification, search |
| **Project** | 19 | 11 | Gestion projets, analytics |
| **Assignment** | 22 | 14 | Gestion affectations, calendrier |

---

## 🚀 Utilisation en Service

Les repositories seront utilisés dans la couche Service :

```java
@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("Employee not found"));
    }
    
    public List<Employee> getProjectTeam(Long projectId) {
        return employeeRepository.findByProjectId(projectId);
    }
}
```

---

## ✅ Checklist

- [x] CategoryRepository créé
- [x] EmployeeRepository créé
- [x] ProjectRepository créé
- [x] AssignmentRepository créé
- [x] Annotations @Repository ajoutées
- [x] Méthodes CRUD héritées (automatiques)
- [x] Requêtes personnalisées implémentées
- [x] Commentaires détaillés en français
- [x] Optimisations de performance appliquées
- [x] Exemple d'utilisation fournis

---

## 📈 Prochaine Étape : Services

Créer la couche Service avec la logique métier :
- CategoryService
- EmployeeService
- ProjectService
- AssignmentService
- AuthenticationService

---

**Phase 2 : ✅ Repositories Spring Data JPA Complétés**

Temps estimé pour cette phase : **30-45 minutes** ✅

Prochaine phase : Services métier (~1-2 heures)
