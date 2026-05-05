import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Assignment } from '../../models/business.model';
import { AssignmentService } from '../../services/assignment.service';
import { AssignmentFormComponent } from './assignment-form/assignment-form.component';

@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements OnInit {
  assignments: Assignment[] = [];
  displayedColumns: string[] = ['employee', 'project', 'period', 'actions'];
  showAllAssignments = false;

  constructor(
    private assignmentService: AssignmentService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAssignments();
  }

  openAddAssignmentDialog(): void {
    const dialogRef = this.dialog.open(AssignmentFormComponent, {
      width: '400px',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createAssignment(result);
      }
    });
  }

  openEditAssignmentDialog(assignment: Assignment): void {
    const dialogRef = this.dialog.open(AssignmentFormComponent, {
      width: '400px',
      data: { mode: 'edit', assignment }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.updateAssignment(assignment.id!, result);
      }
    });
  }

  deleteAssignment(assignment: Assignment): void {
    const employeeName = assignment.employeeName || `Employe #${assignment.employeeId}`;
    const projectName = assignment.projectTitle || `Projet #${assignment.projectId}`;
    
    if (confirm(`Êtes-vous sûr de vouloir supprimer l'affectation de "${employeeName}" à "${projectName}" ?`)) {
      this.assignmentService.delete(assignment.id!).subscribe(
        () => {
          this.snackBar.open('Affectation supprimée avec succès', 'Fermer', { duration: 3000 });
          this.loadAssignments();
        },
        error => {
          this.snackBar.open('Erreur lors de la suppression de l\'affectation', 'Fermer', { duration: 3000 });
        }
      );
    }
  }

  getEmployeeLabel(assignment: Assignment): string {
    return assignment.employeeName || `Employe #${assignment.employeeId}`;
  }

  getProjectLabel(assignment: Assignment): string {
    return assignment.projectTitle || `Projet #${assignment.projectId}`;
  }

  private createAssignment(data: any): void {
    this.assignmentService.create(data).subscribe(
      () => {
        this.snackBar.open('Affectation créée avec succès', 'Fermer', { duration: 3000 });
        this.loadAssignments();
      },
      error => {
        this.snackBar.open('Erreur lors de la création de l\'affectation', 'Fermer', { duration: 3000 });
      }
    );
  }

  private updateAssignment(id: number, data: any): void {
    this.assignmentService.update(id, data).subscribe(
      () => {
        this.snackBar.open('Affectation mise à jour avec succès', 'Fermer', { duration: 3000 });
        this.loadAssignments();
      },
      error => {
        this.snackBar.open('Erreur lors de la mise à jour de l\'affectation', 'Fermer', { duration: 3000 });
      }
    );
  }

  private loadAssignments(): void {
    this.assignmentService.getAll().subscribe(
      assignments => this.assignments = this.sortAssignmentsByStatus(assignments)
    );
  }

  private sortAssignmentsByStatus(assignments: Assignment[]): Assignment[] {
    return [...assignments].sort((a, b) => {
      const activeDiff = Number(this.isActiveNow(b)) - Number(this.isActiveNow(a));

      if (activeDiff !== 0) {
        return activeDiff;
      }

      return new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
    });
  }

  private isActiveNow(assignment: Assignment): boolean {
    const today = new Date();
    const start = new Date(assignment.startDate);
    const end = new Date(assignment.endDate);

    return start <= today && end >= today;
  }

  toggleAssignments(): void {
    this.showAllAssignments = !this.showAllAssignments;
  }

  getDisplayedAssignments(): Assignment[] {
    return this.showAllAssignments ? this.assignments : this.assignments.slice(0, 3);
  }
}
