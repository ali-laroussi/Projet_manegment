# 🚀 Guide de Démarrage Rapide

## Phase 1 : Configuration Backend ✅ COMPLÉTÉE

### 1. Prérequis
- ✅ Java 11 ou supérieur (`java -version`)
- ✅ Maven 3.6+ (`mvn -version`)
- ✅ MySQL 8.0+ (`mysql --version`)
- ✅ IDE : IntelliJ IDEA, VS Code ou Eclipse

### 2. Structure Créée

```
project-management-app/
├── backend/
│   ├── src/main/java/com/projectmanagement/
│   │   └── entity/                    ✅ COMPLÉTÉ
│   │       ├── Category.java
│   │       ├── Employee.java
│   │       ├── Project.java
│   │       ├── Assignment.java
│   │       └── UserRole.java
│   ├── src/main/resources/
│   │   └── application.properties     ✅ COMPLÉTÉ
│   ├── pom.xml                        ✅ COMPLÉTÉ
│   └── ProjectManagementApplication.java ✅ COMPLÉTÉ
│
├── README.md                          ✅ COMPLÉTÉ
├── ENTITIES_SUMMARY.md                ✅ COMPLÉTÉ
└── .gitignore                         ✅ COMPLÉTÉ
```

### 3. Étapes de Démarrage

#### Étape 1 : Préparer la Base de Données

```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter dans MySQL
CREATE DATABASE project_management_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

exit;
```

#### Étape 2 : Configurer le Backend

Éditer `backend/src/main/resources/application.properties` :

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/project_management_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root

# Autres paramètres
jwt.secret=your_super_secret_key_change_in_production
jwt.expiration=86400000
```

#### Étape 3 : Compiler et Démarrer

```bash
# Naviguer au répertoire backend
cd backend

# Compiler le projet
mvn clean compile

# Démarrer l'application
mvn spring-boot:run
```

**Résultat attendu** :
```
Started ProjectManagementApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

#### Étape 4 : Vérifier l'API

```bash
# Vérifier que le serveur est actif
curl http://localhost:8080/api

# Vous devriez recevoir une réponse HTTP
```

---

## Entités Créées

### 📋 Category
**Description** : Catégorie d'employés (Developer, Manager, etc.)

**Colonnes** :
- `id` (INT, PK, AUTO_INCREMENT)
- `name` (VARCHAR(100), UNIQUE, NOT NULL)

**Relations** :
- OneToMany → Employee

---

### 👤 Employee
**Description** : Employé du système avec authentification

**Colonnes** :
- `id` (INT, PK, AUTO_INCREMENT)
- `first_name` (VARCHAR(100), NOT NULL)
- `last_name` (VARCHAR(100), NOT NULL)
- `email` (VARCHAR(150), UNIQUE, NOT NULL)
- `password` (VARCHAR(255), NOT NULL)
- `role` (ENUM('ADMIN','EMPLOYEE'), NOT NULL)
- `category_id` (INT, FK, NOT NULL)

**Index** :
- Email (UNIQUE)
- Category (FK)

**Relations** :
- ManyToOne ← Category
- OneToMany → Assignment

---

### 📁 Project
**Description** : Projet de l'organisation

**Colonnes** :
- `id` (INT, PK, AUTO_INCREMENT)
- `title` (VARCHAR(200), NOT NULL)
- `description` (LONGTEXT)
- `start_date` (DATE, NOT NULL)
- `end_date` (DATE, NOT NULL)

**Index** :
- Title (for search optimization)

**Relations** :
- OneToMany → Assignment

---

### 📌 Assignment
**Description** : Affectation d'un employé à un projet

**Colonnes** :
- `id` (INT, PK, AUTO_INCREMENT)
- `employee_id` (INT, FK, NOT NULL)
- `project_id` (INT, FK, NOT NULL)
- `start_date` (DATE, NOT NULL)
- `end_date` (DATE, NOT NULL)

**Index** :
- Employee (FK)
- Project (FK)
- Composite (employee_id, project_id)

**Relations** :
- ManyToOne → Employee
- ManyToOne → Project

---

## SQL Généré Automatiquement

Hibernate générera automatiquement le schéma SQL suivant :

```sql
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    UNIQUE KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    category_id INT NOT NULL,
    UNIQUE KEY idx_email (email),
    KEY idx_category (category_id),
    CONSTRAINT fk_employee_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description LONGTEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    KEY idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    project_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    KEY idx_employee (employee_id),
    KEY idx_project (project_id),
    KEY idx_employee_project (employee_id, project_id),
    CONSTRAINT fk_assignment_employee FOREIGN KEY (employee_id) 
        REFERENCES employees(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_project FOREIGN KEY (project_id) 
        REFERENCES projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## Dépendances Maven Incluses

```xml
<!-- Core -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
spring-boot-starter-validation

<!-- Database -->
mysql-connector-java 8.0.33

<!-- Authentication -->
jjwt 0.11.5

<!-- Utilities -->
lombok 1.18.30
modelmapper 3.1.1

<!-- Testing -->
spring-boot-starter-test
spring-security-test
```

---

## Points de Contrôle

### ✅ Phase 1 : Entités JPA
- [x] Créer Category.java
- [x] Créer Employee.java
- [x] Créer Project.java
- [x] Créer Assignment.java
- [x] Créer UserRole.java
- [x] Ajouter annotations JPA
- [x] Ajouter annotations Lombok
- [x] Créer pom.xml
- [x] Créer application.properties
- [x] Créer ProjectManagementApplication.java

### ⏳ Phase 2 : Repositories
- [ ] CategoryRepository
- [ ] EmployeeRepository
- [ ] ProjectRepository
- [ ] AssignmentRepository

### ⏳ Phase 3 : Services
- [ ] CategoryService
- [ ] EmployeeService
- [ ] ProjectService
- [ ] AssignmentService
- [ ] AuthenticationService

### ⏳ Phase 4 : Controllers
- [ ] AuthController
- [ ] AdminController
- [ ] EmployeeController

### ⏳ Phase 5 : Security
- [ ] JwtTokenProvider
- [ ] JwtAuthenticationFilter
- [ ] SecurityConfig
- [ ] CustomUserDetailsService

### ⏳ Phase 6 : DTOs & Mappers
- [ ] DTOs (Request/Response)
- [ ] Mappers
- [ ] Validators

### ⏳ Phase 7 : Exception Handling
- [ ] GlobalExceptionHandler
- [ ] Custom Exceptions

### ⏳ Phase 8 : Frontend Angular
- [ ] Structure Angular
- [ ] Login Component
- [ ] Dashboard Admin
- [ ] Dashboard Employee

---

## Fichiers de Documentation

### 📚 Fichiers Créés
1. **README.md** - Vue d'ensemble complète du projet
2. **ENTITIES_DOCUMENTATION.md** - Documentation détaillée des entités
3. **ENTITIES_SUMMARY.md** - Résumé avec diagrammes
4. **QUICKSTART.md** - Ce fichier
5. **.gitignore** - Configuration Git

---

## Commandes Utiles

```bash
# Compiler le projet
mvn clean compile

# Exécuter les tests
mvn test

# Créer un JAR exécutable
mvn clean package

# Démarrer en mode développement
mvn spring-boot:run

# Nettoyer les artefacts de build
mvn clean

# Vérifier les dépendances
mvn dependency:tree
```

---

## Configuration IDE

### IntelliJ IDEA
1. Ouvrir `File` → `Open` → Sélectionner le dossier `backend`
2. Maven auto-configure les dépendances
3. Right-click sur `ProjectManagementApplication.java` → `Run`

### VS Code
1. Installer les extensions :
   - Extension Pack for Java (Microsoft)
   - Spring Boot Extension Pack (Pivotal Software)
2. Ouvrir le dossier `backend`
3. Terminal : `mvn spring-boot:run`

### Eclipse
1. `File` → `Import` → `Existing Maven Projects`
2. Sélectionner le dossier `backend`
3. Right-click sur projet → `Run As` → `Maven build`

---

## Diagnostiquer les Problèmes

### Erreur : "Database connection failed"
```
Solution :
1. Vérifier que MySQL est démarré
2. Vérifier les credentials dans application.properties
3. Vérifier que la base de données existe
```

### Erreur : "Cannot find symbol"
```
Solution :
1. Nettoyer le build : mvn clean
2. Régénérer les sources : mvn compile
3. Rafraîchir le projet (IDE)
```

### Erreur : "Port already in use"
```
Solution :
Changer le port dans application.properties :
server.port=8081
```

### Erreur : "Lombok not working"
```
Solution :
1. Installer Lombok dans l'IDE
2. Activer les annotation processors
3. Redémarrer l'IDE
```

---

## Prochaines Étapes

Une fois cette phase complétée et vérifiée :

1. **Créer les Repositories** (Spring Data JPA)
2. **Implémenter les Services** (logique métier)
3. **Créer les Controllers** (endpoints REST)
4. **Configurer la Security** (JWT)
5. **Créer les DTOs** (validation)
6. **Tester avec Postman**
7. **Développer le Frontend Angular**

---

## Ressources Utiles

- 📖 [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- 📖 [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- 📖 [Spring Security](https://spring.io/projects/spring-security)
- 📖 [Lombok](https://projectlombok.org/)
- 📖 [JJWT (JWT Library)](https://github.com/jwtk/jjwt)

---

**État** : Phase 1 ✅ Complétée  
**Version** : 1.0.0  
**Date** : April 2026
