# Project Management Frontend - Angular

## Vue d'ensemble

Frontend Angular professionnel pour l'application de gestion de projets. Connecté à l'API REST Spring Boot.

## Technologies utilisées

- **Angular 16** - Framework frontend
- **Angular Material** - Composants UI
- **TypeScript** - Langage de programmation
- **RxJS** - Programmation réactive
- **Reactive Forms** - Gestion des formulaires
- **Angular Router** - Navigation
- **JWT Authentication** - Authentification sécurisée
- **SCSS** - Styles avancés

## Architecture

```
src/app/
├── auth/                    # Module d'authentification
│   └── login/              # Composant de connexion
├── admin/                  # Module administrateur
│   ├── dashboard/          # Tableau de bord admin
│   ├── employees/          # Gestion des employés
│   ├── categories/         # Gestion des catégories
│   ├── projects/           # Gestion des projets
│   └── assignments/        # Gestion des affectations
├── employee/               # Module employé
│   ├── dashboard/          # Tableau de bord employé
│   └── my-projects/        # Mes projets
├── services/               # Services API
│   ├── auth.service.ts    # Service d'authentification
│   ├── employee.service.ts
│   ├── category.service.ts
│   ├── project.service.ts
│   └── assignment.service.ts
├── guards/                 # Route guards
│   ├── auth.guard.ts      # Protection des routes
│   └── admin.guard.ts     # Protection admin
├── interceptors/           # HTTP interceptors
│   └── jwt.interceptor.ts # Injection du JWT
├── models/                 # Interfaces TypeScript
│   ├── auth.model.ts
│   └── business.model.ts
└── shared/                 # Composants partagés
```

## Installation

```bash
# Installer les dépendances
npm install

# Démarrer le serveur de développement
npm start

# Compiler pour la production
npm run build
```

## Configuration API

L'application se connecte à l'API backend sur `http://localhost:8080/api`

### Points d'accès API

- `POST /auth/login` - Connexion
- `POST /auth/register` - Inscription
- `GET /employees` - Liste des employés
- `GET /projects` - Liste des projets
- `GET /assignments` - Liste des affectations
- `GET /admin/*` - Endpoints réservés aux admins

## Authentification

### Connexion

Les identifiants de démonstration sont :

**Admin:**
- Email: `admin@company.com`
- Mot de passe: `password123`

**Employé:**
- Email: `alice.dupont@company.com`
- Mot de passe: `password123`

### JWT Token

Le token JWT est stocké en localStorage et envoyé automatiquement dans chaque requête HTTP via l'intercepteur `JwtInterceptor`.

## Guards

- **AuthGuard** - Protège les routes authentifiées
- **AdminGuard** - Protège les routes réservées aux admins

## Services

### AuthService
- `login()` - Connexion utilisateur
- `logout()` - Déconnexion
- `getToken()` - Récupère le token JWT
- `getCurrentUser()` - Utilisateur connecté
- `isAdmin()` - Vérifie si admin
- `isTokenExpired()` - Vérifie expiration du token

### EmployeeService
- `getAll()` - Liste tous les employés
- `getById()` - Récupère un employé
- `create()` - Crée un employé
- `update()` - Modifie un employé
- `delete()` - Supprime un employé

### ProjectService
- `getAll()` - Liste tous les projets
- `getById()` - Récupère un projet
- `create()` - Crée un projet
- `update()` - Modifie un projet
- `delete()` - Supprime un projet

### AssignmentService
- `getAll()` - Liste toutes les affectations
- `create()` - Crée une affectation
- `update()` - Modifie une affectation
- `delete()` - Supprime une affectation

## Composants

### LoginComponent
Formulaire de connexion avec validation réactive et gestion d'erreurs.

### Admin Dashboard
Tableau de bord avec statistiques et accès aux modules CRUD.

### Employee Dashboard
Affichage des projets assignés et informations personnelles.

## Styles

- **Theme Material** - Thème Indigo Pink
- **Responsive Design** - Compatible mobile/tablet/desktop
- **Dark Mode** - Support du thème sombre
- **SCSS** - Préprocesseur CSS avancé

## Validation des formulaires

Tous les formulaires utilisent `Reactive Forms` avec validation :
- Email requis et formaté
- Mots de passe obligatoires (min 6 caractères)
- Dates de début/fin validées
- Messages d'erreur contextuels

## Code Quality

- **TypeScript Strict Mode** - Vérification de type stricte
- **Angular Best Practices** - Conventions Angular 16
- **Reactive Programming** - RxJS et observables
- **Clean Code** - Code modulaire et maintenable
- **Comments in French** - Commentaires en français

## Déploiement

```bash
# Production build
npm run build

# Les fichiers compilés seront dans le dossier dist/
```

## Support

Pour plus d'informations, consultez la documentation Angular officielle:
- [Angular Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)

## Licence

MIT License
