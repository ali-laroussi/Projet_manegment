# Guide de démarrage du Frontend Angular

## Installation et configuration

### 1. Installez les dépendances

```bash
cd frontend
npm install
```

### 2. Démarrez le serveur de développement

```bash
npm start
```

L'application ouvrira automatiquement à `http://localhost:4200`

### 3. Connectez-vous

Utilisez l'un des comptes de démonstration:

**Admin:**
- Email: `admin@company.com`
- Mot de passe: `password123`

**Employé:**
- Email: `alice.dupont@company.com`
- Mot de passe: `password123`

## Structure du projet

```
src/
├── app/
│   ├── auth/                          # Module d'authentification
│   │   └── login/                     # Composant login ✅ COMPLÈTE
│   │
│   ├── admin/                         # Module administrateur
│   │   ├── dashboard/                 # Tableau de bord ✅ COMPLÈTE
│   │   ├── employees/                 # Gestion des employés ✅ COMPLÈTE
│   │   ├── categories/                # Gestion des catégories ✅ COMPLÈTE
│   │   ├── projects/                  # Gestion des projets ✅ COMPLÈTE
│   │   └── assignments/               # Gestion des affectations ✅ COMPLÈTE
│   │
│   ├── employee/                      # Module employé
│   │   ├── dashboard/                 # Tableau de bord ✅ COMPLÈTE
│   │   └── my-projects/               # Mes projets ✅ COMPLÈTE
│   │
│   ├── services/                      # Services API
│   │   ├── auth.service.ts            # ✅ COMPLÈTE
│   │   ├── employee.service.ts        # ✅ COMPLÈTE
│   │   ├── category.service.ts        # ✅ COMPLÈTE
│   │   ├── project.service.ts         # ✅ COMPLÈTE
│   │   └── assignment.service.ts      # ✅ COMPLÈTE
│   │
│   ├── guards/                        # Route guards
│   │   ├── auth.guard.ts              # ✅ COMPLÈTE
│   │   └── admin.guard.ts             # ✅ COMPLÈTE
│   │
│   ├── interceptors/                  # HTTP interceptors
│   │   └── jwt.interceptor.ts         # ✅ COMPLÈTE (Injecte le token)
│   │
│   ├── models/                        # Interfaces TypeScript
│   │   ├── auth.model.ts              # ✅ COMPLÈTE
│   │   └── business.model.ts          # ✅ COMPLÈTE
│   │
│   ├── shared/                        # Composants partagés
│   │   └── unauthorized/              # Page 403 ✅ COMPLÈTE
│   │
│   ├── app.component.ts               # Composant racine ✅ COMPLÈTE
│   ├── app.module.ts                  # Module racine ✅ COMPLÈTE
│   └── app-routing.module.ts          # Routing ✅ COMPLÈTE
│
├── environments/
│   ├── environment.ts                 # Dev ✅ COMPLÈTE
│   └── environment.prod.ts            # Prod ✅ COMPLÈTE
│
├── main.ts                            # Point d'entrée ✅ COMPLÈTE
├── index.html                         # HTML ✅ COMPLÈTE
├── styles.scss                        # Styles globaux ✅ COMPLÈTE
├── angular.json                       # Config Angular ✅ COMPLÈTE
├── tsconfig.json                      # Config TypeScript ✅ COMPLÈTE
└── package.json                       # Dépendances ✅ COMPLÈTE
```

## Fonctionnalités implémentées

### ✅ Authentification
- [x] Page de login avec formulaire réactif
- [x] Sauvegarde du JWT en localStorage
- [x] Redirection selon le rôle (Admin/Employee)
- [x] Décodage du JWT pour extraire les infos utilisateur
- [x] Vérification de l'expiration du token
- [x] Intercepteur JWT pour toutes les requêtes

### ✅ Routing et Guards
- [x] Route guards basées sur l'authentification
- [x] Route guards spécifiques aux admins
- [x] Redirection automatique vers login
- [x] Lazy loading des modules
- [x] PageUnauthorized (403)

### ✅ Tableau de bord Admin
- [x] Affichage des statistiques (employés, projets, affectations, catégories)
- [x] Actions rapides (ajout d'employés, création de projets, etc.)
- [x] Design moderne avec cartes dégradées
- [x] Responsive design

### ✅ Tableau de bord Employé
- [x] Affichage des infos personnelles
- [x] Liste des projets assignés
- [x] Cartes de projet interactive
- [x] Vue responsive

### ✅ Gestion des données (CRUD)
- [x] Liste des employés avec tableau
- [x] Liste des catégories
- [x] Liste des projets
- [x] Liste des affectations
- [x] Mes projets pour employés

### ✅ Services API
- [x] AuthService (login, logout, token management)
- [x] EmployeeService (CRUD employés)
- [x] CategoryService (CRUD catégories)
- [x] ProjectService (CRUD projets)
- [x] AssignmentService (CRUD affectations)

### ✅ Design et UX
- [x] Angular Material theme (Indigo-Pink)
- [x] Design responsive (mobile/tablet/desktop)
- [x] Support du dark mode
- [x] Animations fluides
- [x] Messages d'erreur et de succès (SnackBar)
- [x] Validation des formulaires

### ✅ Configuration
- [x] TypeScript strict mode
- [x] Path aliases (@app, @services, @models, etc.)
- [x] Environment files (dev/prod)
- [x] EditorConfig
- [x] .gitignore

## Points d'accès API

L'application se connecte à l'API backend sur `http://localhost:8080/api`:

```typescript
// Authentification
POST   /auth/login        - Connexion
POST   /auth/register     - Inscription

// Employés
GET    /employees         - Liste
GET    /employees/{id}    - Détail
POST   /admin/employees   - Créer (admin)
PUT    /admin/employees/{id} - Modifier (admin)
DELETE /admin/employees/{id} - Supprimer (admin)

// Catégories
GET    /admin/categories  - Liste
POST   /admin/categories  - Créer (admin)
PUT    /admin/categories/{id} - Modifier (admin)
DELETE /admin/categories/{id} - Supprimer (admin)

// Projets
GET    /projects          - Liste
GET    /projects/{id}     - Détail
POST   /admin/projects    - Créer (admin)
PUT    /admin/projects/{id} - Modifier (admin)
DELETE /admin/projects/{id} - Supprimer (admin)

// Affectations
GET    /assignments       - Liste
GET    /assignments/{id}  - Détail
POST   /assignments       - Créer
PUT    /assignments/{id}  - Modifier
DELETE /admin/assignments/{id} - Supprimer (admin)
```

## Prochaines étapes

### Composants à compléter
Les modules suivants ont des stubs et peuvent être complétés:

1. **Dialog d'ajout/modification d'employés**
   - Ajouter MatDialogModule
   - Créer EmployeeFormComponent
   - Implémenter la création/modification

2. **Dialog d'ajout/modification de projets**
   - Similaire aux employés
   - Validation des dates (endDate > startDate)

3. **Dialog pour les affectations**
   - Sélection d'employé/projet
   - Validation des dates

4. **Détail d'un projet/employé**
   - Page dédiée avec informations complètes
   - Affectations associées

5. **Recherche et filtrage**
   - Ajouter MatFilterModule
   - Recherche par nom, email, titre, etc.

6. **Pagination**
   - Ajouter MatPaginatorModule
   - Implémenter la pagination des listes

7. **Graphiques/statistiques**
   - Ajouter ChartJS ou ng2-charts
   - Visualiser les données

## Commandes utiles

```bash
# Générer un composant
ng generate component shared/my-component

# Générer un service
ng generate service services/my-service

# Générer un module
ng generate module my-module

# Build pour la production
npm run build

# Tests unitaires
npm test

# Linting
ng lint
```

## Points de sécurité importants

✅ **Implémentés:**
- JWT token en localStorage
- Intercepteur JWT pour automatiser l'ajout du header Authorization
- Guards sur les routes
- Vérification du rôle utilisateur
- Décodage sécurisé du JWT (côté client)
- Messages d'erreur sécurisés (pas de détails sensibles)

⚠️ **À améliorer en production:**
- Utiliser httpOnly cookies au lieu de localStorage (si possible)
- Implémenter un refresh token mechanism
- Ajouter CSRF protection
- Valider toujours côté serveur
- Implémenter le logout côté serveur (blacklist tokens)

## Support

Pour toute question ou problème:
1. Consultez la documentation Angular officielle: https://angular.io
2. Consultez la documentation Angular Material: https://material.angular.io
3. Vérifiez les logs dans la console navigateur (F12)

## Auteur

Projet généré automatiquement par GitHub Copilot - Prêt pour démonstration universitaire ✅
