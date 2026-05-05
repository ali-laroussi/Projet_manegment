import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { forkJoin, interval, Subject } from 'rxjs';
import { takeUntil, switchMap } from 'rxjs/operators';
import { EmployeeService } from '../../services/employee.service';
import { ProjectService } from '../../services/project.service';
import { AssignmentService } from '../../services/assignment.service';
import { CategoryService } from '../../services/category.service';
import { Employee, Project, Assignment, Category } from '../../models/business.model';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  private refreshInterval = 5000; // Rafraîchir toutes les 5 secondes
  employeeCount = 0;
  projectCount = 0;
  assignmentCount = 0;
  categoryCount = 0;
  adminCount = 0;
  activeProjectCount = 0;
  activeProjects: Project[] = [];
  completedProjectCount = 0;
  completedProjects: Project[] = [];
  activeAssignmentCount = 0;
  totalVolume = 0;
  leadCategoryName = 'Aucune categorie';
  summaryCaption = 'Chargement des donnees du workspace...';
  employeeInsight = 'Chargement des profils et roles actifs.';
  projectInsight = 'Chargement des initiatives et du portefeuille.';
  assignmentInsight = 'Chargement des affectations en cours.';
  categoryInsight = 'Chargement du catalogue des categories.';
  lastUpdateTime = new Date();
  isLiveUpdating = false;
  showAllActiveProjects = false;
  showAllCompletedProjects = false;

  // Chart data
  projectStatusChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };
  
  employeeRoleChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };
  
  assignmentStatusChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };
  
  categoryDistributionChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: [],
    datasets: []
  };

  // Chart options
  doughnutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
      legend: {
        position: 'bottom',
        onClick: () => null,
        labels: {
          usePointStyle: true,
          padding: 15,
          font: {
            size: 12
          }
        }
      }
    }
  };

  constructor(
    private employeeService: EmployeeService,
    private projectService: ProjectService,
    private assignmentService: AssignmentService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    // Charger les données immédiatement
    this.loadDashboardData();

    // Configurer le rafraîchissement automatique toutes les 5 secondes
    interval(this.refreshInterval)
      .pipe(
        switchMap(() => forkJoin({
          employees: this.employeeService.getAll(),
          projects: this.projectService.getAll(),
          assignments: this.assignmentService.getAll(),
          categories: this.categoryService.getAll()
        })),
        takeUntil(this.destroy$)
      )
      .subscribe(({ employees, projects, assignments, categories }) => {
        this.updateDashboardData(employees, projects, assignments, categories);
      });
  }

  ngOnDestroy(): void {
    // Arrêter les observables
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadDashboardData(): void {
    forkJoin({
      employees: this.employeeService.getAll(),
      projects: this.projectService.getAll(),
      assignments: this.assignmentService.getAll(),
      categories: this.categoryService.getAll()
    }).pipe(
      takeUntil(this.destroy$)
    ).subscribe(({ employees, projects, assignments, categories }) => {
      this.updateDashboardData(employees, projects, assignments, categories);
    });
  }

  private updateDashboardData(employees: Employee[], projects: Project[], assignments: Assignment[], categories: Category[]): void {
    // Mettre à jour l'indicateur de temps réel
    this.lastUpdateTime = new Date();
    this.isLiveUpdating = true;

    this.employeeCount = employees.length;
    this.projectCount = projects.length;
    this.assignmentCount = assignments.length;
    this.categoryCount = categories.length;

    this.adminCount = employees.filter(employee => employee.role === 'ADMIN').length;
    this.activeProjects = projects
      .filter(project => this.isActiveNow(project.startDate, project.endDate))
      .sort((a, b) => new Date(a.endDate).getTime() - new Date(b.endDate).getTime());
    this.activeProjectCount = this.activeProjects.length;
    this.completedProjects = projects
      .filter(project => this.isFinished(project.endDate))
      .sort((a, b) => new Date(b.endDate).getTime() - new Date(a.endDate).getTime());
    this.completedProjectCount = this.completedProjects.length;
    this.activeAssignmentCount = assignments.filter(assignment => this.isActiveNow(assignment.startDate, assignment.endDate)).length;
    this.totalVolume = this.employeeCount + this.projectCount + this.assignmentCount + this.categoryCount;
    this.leadCategoryName = this.getLeadCategoryName(employees, categories);

    this.summaryCaption = `${this.projectCount} projets, ${this.assignmentCount} affectations et ${this.employeeCount} profils synchronises depuis l'API.`;
    this.employeeInsight = `${this.adminCount} administrateur(s) et ${this.employeeCount - this.adminCount} employe(s) actifs.`;
    this.projectInsight = `${this.activeProjectCount} projet(s) actif(s) et ${this.completedProjectCount} cloture(s) selon les dates backend.`;
    this.assignmentInsight = `${this.activeAssignmentCount} affectation(s) actuellement en cours dans les donnees.`;
    this.categoryInsight = this.buildCategoryInsight(employees, categories);

    // Initialiser les graphiques avec les nouvelles données
    this.initializeCharts(employees, projects, assignments, categories);

    // Réinitialiser l'indicateur après 1 seconde
    setTimeout(() => {
      this.isLiveUpdating = false;
    }, 1000);
  }

  private initializeCharts(employees: Employee[], projects: Project[], assignments: Assignment[], categories: Category[]): void {
    // Project Status Chart
    const inactiveProjectCount = Math.max(this.projectCount - this.activeProjectCount - this.completedProjectCount, 0);
    this.projectStatusChartData = {
      labels: ['Actifs', 'Termines', 'Autres'],
      datasets: [{
        data: [this.activeProjectCount, this.completedProjectCount, inactiveProjectCount],
        backgroundColor: ['#4CAF50', '#2196F3', '#FFC107'],
        borderColor: ['#45a049', '#0b7dda', '#ffb300'],
        borderWidth: 2
      }]
    };

    // Employee Role Chart
    const employeeCount = this.employeeCount - this.adminCount;
    this.employeeRoleChartData = {
      labels: ['Administrateurs', 'Employes'],
      datasets: [{
        data: [this.adminCount, employeeCount],
        backgroundColor: ['#E91E63', '#00BCD4'],
        borderColor: ['#c2185b', '#0097a7'],
        borderWidth: 2
      }]
    };

    // Assignment Status Chart
    const inactiveAssignmentCount = this.assignmentCount - this.activeAssignmentCount;
    this.assignmentStatusChartData = {
      labels: ['En cours', 'Terminees'],
      datasets: [{
        data: [this.activeAssignmentCount, inactiveAssignmentCount],
        backgroundColor: ['#FF9800', '#9C27B0'],
        borderColor: ['#e68900', '#7b1fa2'],
        borderWidth: 2
      }]
    };

    // Category Distribution Chart based on employees effectively assigned to each category
    const employeeCategoryDistribution = categories
      .map(category => ({
        name: category.name,
        count: employees.filter(employee => employee.categoryId === category.id).length
      }))
      .filter(category => category.count > 0);

    this.categoryDistributionChartData = {
      labels: employeeCategoryDistribution.length > 0
        ? employeeCategoryDistribution.map(category => category.name)
        : ['Aucune categorie employee'],
      datasets: [{
        data: employeeCategoryDistribution.length > 0
          ? employeeCategoryDistribution.map(category => category.count)
          : [0],
        backgroundColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
          '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#FF6384'
        ],
        borderColor: [
          '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF',
          '#FF9F40', '#FF6384', '#C9CBCF', '#4BC0C0', '#FF6384'
        ],
        borderWidth: 2
      }]
    };
  }

  private getLeadCategoryName(employees: Employee[], categories: Category[]): string {
    const categoryCounts = categories
      .map(category => ({
        name: category.name,
        count: employees.filter(employee => employee.categoryId === category.id).length
      }))
      .sort((a, b) => b.count - a.count);

    if (!categoryCounts.length || categoryCounts[0].count === 0) {
      return 'Aucune categorie';
    }

    return categoryCounts[0].name;
  }

  private buildCategoryInsight(employees: Employee[], categories: Category[]): string {
    const usedCategories = categories.filter(category =>
      employees.some(employee => employee.categoryId === category.id)
    );

    if (!usedCategories.length) {
      return 'Aucun employe n est actuellement rattache a une categorie.';
    }

    const leadCategory = this.getLeadCategoryName(employees, categories);
    return `${usedCategories.length} categorie(s) utilisee(s) par les employes. Categorie dominante: ${leadCategory}.`;
  }

  private isActiveNow(startDate: string, endDate: string): boolean {
    const today = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    return start <= today && end >= today;
  }

  private isFinished(endDate: string): boolean {
    return new Date(endDate) < new Date();
  }

  toggleActiveProjects(): void {
    this.showAllActiveProjects = !this.showAllActiveProjects;
  }

  toggleCompletedProjects(): void {
    this.showAllCompletedProjects = !this.showAllCompletedProjects;
  }

  getDisplayedActiveProjects(): Project[] {
    return this.showAllActiveProjects ? this.activeProjects : this.activeProjects.slice(0, 3);
  }

  getDisplayedCompletedProjects(): Project[] {
    return this.showAllCompletedProjects ? this.completedProjects : this.completedProjects.slice(0, 3);
  }
}
