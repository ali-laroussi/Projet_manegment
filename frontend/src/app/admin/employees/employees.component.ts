import { Component, OnInit } from '@angular/core';
import { Employee, CreateEmployeeRequest } from '../../models/business.model';
import { EmployeeService } from '../../services/employee.service';
import { NotificationService } from '../../services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { EmployeeFormComponent } from './employee-form/employee-form.component';

@Component({
  selector: 'app-employees',
  templateUrl: './employees.component.html',
  styleUrls: ['./employees.component.css']
})
export class EmployeesComponent implements OnInit {
  employees: Employee[] = [];
  displayedColumns: string[] = ['name', 'email', 'role', 'actions'];
  isLoading = false;
  showAllEmployees = false;

  constructor(
    private employeeService: EmployeeService,
    private notificationService: NotificationService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
  }

  getRoleCount(role: 'ADMIN' | 'EMPLOYEE'): number {
    return this.employees.filter(employee => employee.role === role).length;
  }

  openAddEmployeeDialog(): void {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      width: '500px',
      data: { mode: 'create', employee: null }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createEmployee(result);
      }
    });
  }

  openEditEmployeeDialog(employee: Employee): void {
    const dialogRef = this.dialog.open(EmployeeFormComponent, {
      width: '500px',
      data: { mode: 'edit', employee: employee }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.updateEmployee(employee.id, result);
      }
    });
  }

  sendNotification(employee: Employee): void {
    const message = prompt(`Message pour ${employee.firstName} ${employee.lastName}`);

    if (!message || !message.trim()) {
      return;
    }

    this.notificationService.send({ employeeId: employee.id, message: message.trim() }).subscribe({
      next: () => {
        this.snackBar.open('Notification envoyee avec succes', 'Fermer', { duration: 3000 });
      },
      error: (error) => {
        console.error('Erreur lors de l envoi de la notification:', error);
        this.snackBar.open('Erreur lors de l envoi de la notification', 'Fermer', { duration: 3000 });
      }
    });
  }

  deleteEmployee(employee: Employee): void {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer ${employee.firstName} ${employee.lastName} ?`)) {
      return;
    }

    this.isLoading = true;
    this.employeeService.delete(employee.id).subscribe({
      next: () => {
        this.snackBar.open(`${employee.firstName} ${employee.lastName} a été supprimé avec succès`, 'Fermer', { duration: 3000 });
        this.loadEmployees();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la suppression:', error);
        this.snackBar.open('Erreur lors de la suppression de l\'employe', 'Fermer', { duration: 3000 });
      }
    });
  }

  private createEmployee(employeeData: CreateEmployeeRequest): void {
    this.isLoading = true;
    this.employeeService.create(employeeData).subscribe({
      next: (newEmployee) => {
        this.snackBar.open('Employé créé avec succès', 'Fermer', { duration: 3000 });
        this.loadEmployees();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la création:', error);
        this.snackBar.open('Erreur lors de la création de l\'employe', 'Fermer', { duration: 3000 });
      }
    });
  }

  private updateEmployee(id: number, employeeData: CreateEmployeeRequest): void {
    this.isLoading = true;
    this.employeeService.update(id, employeeData).subscribe({
      next: (updatedEmployee) => {
        this.snackBar.open('Employé modifié avec succès', 'Fermer', { duration: 3000 });
        this.loadEmployees();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la modification:', error);
        this.snackBar.open('Erreur lors de la modification de l\'employe', 'Fermer', { duration: 3000 });
      }
    });
  }

  private loadEmployees(): void {
    this.isLoading = true;
    this.employeeService.getAll().subscribe({
      next: (employees) => {
        this.employees = employees;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors du chargement:', error);
        this.snackBar.open('Erreur lors du chargement des employes', 'Fermer', { duration: 3000 });
      }
    });
  }

  toggleEmployees(): void {
    this.showAllEmployees = !this.showAllEmployees;
  }

  getDisplayedEmployees(): Employee[] {
    return this.showAllEmployees ? this.employees : this.employees.slice(0, 3);
  }
}
