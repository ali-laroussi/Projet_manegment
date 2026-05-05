import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, interval, forkJoin } from 'rxjs';
import { switchMap, takeUntil } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { AssignmentService } from '../../services/assignment.service';
import { NotificationService } from '../../services/notification.service';
import { Project, Assignment, AppNotification } from '../../models/business.model';

@Component({
  selector: 'app-employee-dashboard',
  templateUrl: './employee-dashboard.component.html',
  styleUrls: ['./employee-dashboard.component.css']
})
export class EmployeeDashboardComponent implements OnInit, OnDestroy {
  currentUser$ = this.authService.currentUser$;
  projects: Project[] = [];
  assignments: Assignment[] = [];
  notifications: AppNotification[] = [];
  projectCount = 0;
  activeAssignmentCount = 0;
  highlightProject: Project | null = null;
  workloadStatus = 'Libre';
  workloadFootnote = 'Aucune affectation active pour le moment.';
  assignmentSummary = 'Chargement des missions depuis le backend.';
  lastUpdateTime = new Date().toLocaleTimeString();
  isLiveUpdating = true;
  showAllNotifications = false;
  showAllProjects = false;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private assignmentService: AssignmentService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadProjectsInitially();
    this.setupLiveUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadProjectsInitially(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    this.loadEmployeeProjectsAndAssignments(user.id);
  }

  private setupLiveUpdates(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    interval(3000)
      .pipe(
        switchMap(() => 
          forkJoin({
            projects: this.projectService.getEmployeeProjects(user.id),
            assignments: this.assignmentService.getByEmployeeId(user.id),
            notifications: this.notificationService.getMine()
          })
        ),
        takeUntil(this.destroy$)
      )
      .subscribe(({ projects, assignments, notifications }) => {
        this.projects = projects;
        this.assignments = assignments;
        this.notifications = notifications;
        this.updateMetrics();
        this.lastUpdateTime = new Date().toLocaleTimeString();
      });
  }

  private loadEmployeeProjectsAndAssignments(employeeId: number): void {
    forkJoin({
      projects: this.projectService.getEmployeeProjects(employeeId),
      assignments: this.assignmentService.getByEmployeeId(employeeId),
      notifications: this.notificationService.getMine()
    }).subscribe(({ projects, assignments, notifications }) => {
      this.projects = projects;
      this.assignments = assignments;
      this.notifications = notifications;
      this.updateMetrics();
      this.lastUpdateTime = new Date().toLocaleTimeString();
    });
  }

  private updateMetrics(): void {
    this.projectCount = this.projects.length;
    this.activeAssignmentCount = this.assignments.filter(assignment =>
      this.isActiveNow(assignment.startDate, assignment.endDate)
    ).length;

    this.highlightProject = this.pickHighlightProject(this.projects, this.assignments);
    this.assignmentSummary = `${this.assignments.length} affectation(s) et ${this.activeAssignmentCount} en cours selon les dates backend.`;

    if (this.activeAssignmentCount > 0) {
      this.workloadStatus = 'En cours';
      this.workloadFootnote = `${this.activeAssignmentCount} mission(s) active(s) actuellement dans votre planning.`;
    } else if (this.assignments.length > 0) {
      this.workloadStatus = 'Planifie';
      this.workloadFootnote = `Des affectations existent, mais aucune n'est active a la date du jour.`;
    } else {
      this.workloadStatus = 'Libre';
      this.workloadFootnote = `Aucune affectation n'a ete retournee par le backend pour votre profil.`;
    }
  }

  private pickHighlightProject(projects: Project[], assignments: Assignment[]): Project | null {
    if (!projects.length) {
      return null;
    }

    const activeAssignment = assignments.find(assignment => this.isActiveNow(assignment.startDate, assignment.endDate));
    if (activeAssignment) {
      return projects.find(project => project.id === activeAssignment.projectId) || projects[0];
    }

    return projects[0];
  }

  private isActiveNow(startDate: string, endDate: string): boolean {
    const today = new Date();
    const start = new Date(startDate);
    const end = new Date(endDate);
    return start <= today && end >= today;
  }

  toggleNotifications(): void {
    this.showAllNotifications = !this.showAllNotifications;
  }

  toggleProjects(): void {
    this.showAllProjects = !this.showAllProjects;
  }

  getDisplayedNotifications(): AppNotification[] {
    return this.showAllNotifications ? this.notifications : this.notifications.slice(0, 4);
  }

  getDisplayedProjects(): Project[] {
    return this.showAllProjects ? this.projects : this.projects.slice(0, 3);
  }
}
