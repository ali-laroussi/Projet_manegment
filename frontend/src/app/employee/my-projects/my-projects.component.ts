import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject, interval, forkJoin } from 'rxjs';
import { switchMap, takeUntil } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { ProjectService } from '../../services/project.service';
import { AssignmentService } from '../../services/assignment.service';
import { Project, Assignment } from '../../models/business.model';

@Component({
  selector: 'app-my-projects',
  templateUrl: './my-projects.component.html',
  styleUrls: ['./my-projects.component.css']
})
export class MyProjectsComponent implements OnInit, OnDestroy {
  projects: Project[] = [];
  assignments: Assignment[] = [];
  lastUpdateTime = new Date().toLocaleTimeString();
  isLiveUpdating = true;

  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private assignmentService: AssignmentService
  ) {}

  ngOnInit(): void {
    this.loadMyProjectsInitially();
    this.setupLiveUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadMyProjectsInitially(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.loadEmployeeProjectsAndAssignments(user.id);
    }
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
            assignments: this.assignmentService.getByEmployeeId(user.id)
          })
        ),
        takeUntil(this.destroy$)
      )
      .subscribe(({ projects, assignments }) => {
        this.projects = projects;
        this.assignments = assignments;
        this.lastUpdateTime = new Date().toLocaleTimeString();
      });
  }

  private loadEmployeeProjectsAndAssignments(employeeId: number): void {
    forkJoin({
      projects: this.projectService.getEmployeeProjects(employeeId),
      assignments: this.assignmentService.getByEmployeeId(employeeId)
    }).subscribe(({ projects, assignments }) => {
      this.projects = projects;
      this.assignments = assignments;
      this.lastUpdateTime = new Date().toLocaleTimeString();
    });
  }

  getAssignmentStatus(projectId: number): 'active' | 'planned' | 'none' {
    const assignment = this.assignments.find(a => a.projectId === projectId);
    if (!assignment) {
      return 'none';
    }

    const today = new Date();
    const start = new Date(assignment.startDate);
    const end = new Date(assignment.endDate);

    if (start <= today && end >= today) {
      return 'active';
    }
    return 'planned';
  }

  getAssignmentDates(projectId: number): { startDate: string; endDate: string } | null {
    const assignment = this.assignments.find(a => a.projectId === projectId);
    if (!assignment) {
      return null;
    }
    return {
      startDate: assignment.startDate,
      endDate: assignment.endDate
    };
  }
}

