# Project Management Application

Application web complète de gestion de projets, employés et affectations construite avec Spring Boot et Angular.

## 📋 Vue d'ensemble

Plateforme centralisée permettant aux administrateurs de gérer les projets, employés et affectations, et aux employés de consulter leurs affectations et les membres de leurs projets.

## 🏗️ Architecture

```
project-management-app/
├── backend/                                    # Application Spring Boot
│   ├── src/main/java/com/projectmanagement/
│   │   ├── entity/                            # Entités JPA
│   │   ├── repository/                        # Spring Data JPA Repositories
│   │   ├── service/                           # Logique métier
│   │   ├── controller/                        # Endpoints REST
│   │   ├── security/                          # Configuration Security & JWT
│   │   ├── dto/                               # Objets de transfert
│   │   ├── mapper/                            # Mappers Entity <-> DTO
│   │   └── exception/                         # Gestion des erreurs
│   ├── src/main/resources/
│   │   └── application.properties             # Configuration Spring Boot
│   └── pom.xml                                # Dépendances Maven
│
└── frontend/                                  # Application Angular
    └── src/
```

## 🛠️ Stack Technologique

### Backend
- **Framework** : Spring Boot 2.7.14
- **Authentification** : Spring Security + JWT (JJWT)
- **Base de données** : MySQL 8
- **Persistence** : Spring Data JPA + Hibernate
- **Mapping** : Lombok + ModelMapper
- **Java** : OpenJDK 11

### Frontend
- **Framework** : Angular 14+
- **Styling** : Angular Material / Bootstrap 5
- **HTTP Client** : Angular HttpClient
- **State Management** : Services + RxJS

## ✨ Fonctionnalités

### Pour les Administrateurs
- ✅ Créer, modifier, supprimer des employés
- ✅ Gérer les catégories d'employés
- ✅ Créer, modifier, supprimer des projets
- ✅ Affecter un employé à un projet
- ✅ Modifier les affectations existantes
- ✅ Supprimer des affectations
- ✅ Voir tous les employés et projets

### Pour les Employés
- ✅ Consulter ses affectations (projets)
- ✅ Voir les détails de ses projets
- ✅ Consulter les employés affectés à ses projets

## 🔐 Sécurité

- Authentification JWT avec refresh tokens
- Protection des endpoints selon le rôle
- Mot de passe hashé en BCrypt
- CORS configuré
- Validation des données côté serveur

## 🚀 Démarrage

### Prérequis
- Java 11 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- Node.js 14+ (pour Angular)
- npm 6+ ou yarn

### Configuration Backend

1. **Créer la base de données**
```sql
CREATE DATABASE project_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **Configurer les paramètres de connexion** dans `backend/src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/project_management_db
spring.datasource.username=root
spring.datasource.password=your_password
```

3. **Compiler et démarrer**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

L'API sera disponible sur `http://localhost:8080/api`

### Configuration Frontend

1. **Installer les dépendances**
```bash
cd frontend
npm install
```

2. **Démarrer le serveur de développement**
```bash
ng serve
```

L'application sera accessible sur `http://localhost:4200`

## 📚 Modèle de Données

### Entités Principales

#### Category
- Représente une catégorie d'employés (Developer, Manager, Designer, etc.)
- Relation OneToMany avec Employee

#### Employee
- Représente un employé du système
- Attributs : firstName, lastName, email, password, role, category
- Relations : ManyToOne avec Category, OneToMany avec Assignment
- Un employé appartient à une seule catégorie

#### Project
- Représente un projet de l'organisation
- Attributs : title, description, startDate, endDate
- Relation OneToMany avec Assignment

#### Assignment
- Représente l'affectation d'un employé à un projet
- Attributs : employee, project, startDate, endDate
- Relations : ManyToOne avec Employee et Project
- Implémente la relation many-to-many entre Employee et Project

## 🔌 API REST

### Authentication Endpoints
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `POST /api/auth/refresh` - Rafraîchir le token

### Employee Endpoints (Admin)
- `GET /api/admin/employees` - Lister tous les employés
- `GET /api/admin/employees/{id}` - Détails d'un employé
- `POST /api/admin/employees` - Créer un employé
- `PUT /api/admin/employees/{id}` - Modifier un employé
- `DELETE /api/admin/employees/{id}` - Supprimer un employé

### Category Endpoints (Admin)
- `GET /api/admin/categories` - Lister toutes les catégories
- `POST /api/admin/categories` - Créer une catégorie
- `PUT /api/admin/categories/{id}` - Modifier une catégorie
- `DELETE /api/admin/categories/{id}` - Supprimer une catégorie

### Project Endpoints (Admin)
- `GET /api/admin/projects` - Lister tous les projets
- `POST /api/admin/projects` - Créer un projet
- `PUT /api/admin/projects/{id}` - Modifier un projet
- `DELETE /api/admin/projects/{id}` - Supprimer un projet

### Assignment Endpoints (Admin)
- `GET /api/admin/assignments` - Lister toutes les affectations
- `POST /api/admin/assignments` - Créer une affectation
- `PUT /api/admin/assignments/{id}` - Modifier une affectation
- `DELETE /api/admin/assignments/{id}` - Supprimer une affectation

### Employee Endpoints (Employee)
- `GET /api/employee/profile` - Consulter son profil
- `GET /api/employee/assignments` - Consulter ses affectations
- `GET /api/employee/projects` - Consulter ses projets

## 📝 Exemples Postman

Voir le fichier `POSTMAN_EXAMPLES.md` pour des exemples complets de requêtes API.

## 🗄️ Structure des Fichiers

```
backend/
├── src/main/java/com/projectmanagement/
│   ├── entity/                    # Entités JPA avec Lombok
│   │   ├── Category.java
│   │   ├── Employee.java
│   │   ├── Project.java
│   │   ├── Assignment.java
│   │   └── UserRole.java
│   ├── repository/                # Spring Data JPA Repositories
│   ├── service/                   # Services métier
│   ├── controller/                # Contrôleurs REST
│   ├── security/                  # Configuration Security
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── SecurityConfig.java
│   │   └── CustomUserDetailsService.java
│   ├── dto/                       # Data Transfer Objects
│   ├── mapper/                    # Mappers Entity/DTO
│   ├── exception/                 # Gestion des exceptions
│   └── ProjectManagementApplication.java
│
├── src/main/resources/
│   └── application.properties
│
├── pom.xml
├── README.md
└── ENTITIES_DOCUMENTATION.md
```

## 🔑 Variables d'Environnement

```properties
# JWT
JWT_SECRET=your_super_secret_key
JWT_EXPIRATION=86400000

# Database
DB_URL=jdbc:mysql://localhost:3306/project_management_db
DB_USERNAME=root
DB_PASSWORD=root

# Server
SERVER_PORT=8080
```

## 📖 Documentation

- [Documentation des Entités](ENTITIES_DOCUMENTATION.md)
- [Exemples API Postman](POSTMAN_EXAMPLES.md)

## ✅ Liste de Contrôle du Développement

- [x] Création des entités JPA
- [ ] Création des repositories
- [ ] Création des services
- [ ] Création des contrôleurs REST
- [ ] Implémentation de la sécurité JWT
- [ ] Création des DTOs
- [ ] Création des mappers
- [ ] Gestion des exceptions globale
- [ ] Scripts SQL initiaux
- [ ] Interface Angular login
- [ ] Dashboard admin
- [ ] Dashboard employé
- [ ] Tests unitaires
- [ ] Tests d'intégration

## 👥 Utilisateurs par Défaut

À créer après démarrage de l'application :

**Admin**
- Email : admin@project-management.com
- Mot de passe : Admin@123456
- Rôle : ADMIN

**Employee**
- Email : employee@project-management.com
- Mot de passe : Employee@123456
- Rôle : EMPLOYEE

## 📄 Licence

Ce projet est fourni à titre éducatif.

## 📞 Support

Pour toute question ou problème, veuillez consulter la documentation ou contacter l'équipe de développement.

---

**Version** : 1.0.0  
**Statut** : En développement  
**Dernière mise à jour** : April 2026
