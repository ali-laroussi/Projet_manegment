import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Project } from '../../models/business.model';
import { ProjectService } from '../../services/project.service';
import { ProjectFormComponent } from './project-form/project-form.component';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {
  projects: Project[] = [];
  displayedColumns: string[] = ['title', 'description', 'dates', 'actions'];
  showAllProjects = false;

  constructor(
    private projectService: ProjectService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  openAddProjectDialog(): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '500px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createProject(result);
      }
    });
  }

  openEditProjectDialog(project: Project): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '500px',
      data: { mode: 'edit', project }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateProject(project.id!, result);
      }
    });
  }

  deleteProject(project: Project): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer le projet "${project.title}" ?`)) {
      this.projectService.delete(project.id!).subscribe(
        () => {
          this.snackBar.open('Projet supprimé avec succès', 'Fermer', { duration: 3000 });
          this.loadProjects();
        },
        error => {
          this.snackBar.open('Erreur lors de la suppression du projet', 'Fermer', { duration: 3000 });
        }
      );
    }
  }

  private createProject(data: any): void {
    this.projectService.create(data).subscribe(
      () => {
        this.snackBar.open('Projet créé avec succès', 'Fermer', { duration: 3000 });
        this.loadProjects();
      },
      error => {
        this.snackBar.open('Erreur lors de la création du projet', 'Fermer', { duration: 3000 });
      }
    );
  }

  private updateProject(id: number, data: any): void {
    this.projectService.update(id, data).subscribe(
      () => {
        this.snackBar.open('Projet mis à jour avec succès', 'Fermer', { duration: 3000 });
        this.loadProjects();
      },
      error => {
        this.snackBar.open('Erreur lors de la mise à jour du projet', 'Fermer', { duration: 3000 });
      }
    );
  }

  private loadProjects(): void {
    this.projectService.getAll().subscribe(
      projects => this.projects = this.sortProjectsByStatus(projects)
    );
  }

  private sortProjectsByStatus(projects: Project[]): Project[] {
    return [...projects].sort((a, b) => {
      const activeDiff = Number(this.isActiveNow(b)) - Number(this.isActiveNow(a));

      if (activeDiff !== 0) {
        return activeDiff;
      }

      return new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
    });
  }

  private isActiveNow(project: Project): boolean {
    const today = new Date();
    const start = new Date(project.startDate);
    const end = new Date(project.endDate);

    return start <= today && end >= today;
  }

  toggleProjects(): void {
    this.showAllProjects = !this.showAllProjects;
  }

  getDisplayedProjects(): Project[] {
    return this.showAllProjects ? this.projects : this.projects.slice(0, 3);
  }
}
