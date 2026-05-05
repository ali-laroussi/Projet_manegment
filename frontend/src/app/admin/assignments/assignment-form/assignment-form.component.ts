import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { EmployeeService } from '../../../services/employee.service';
import { ProjectService } from '../../../services/project.service';

@Component({
  selector: 'app-assignment-form',
  templateUrl: './assignment-form.component.html',
  styleUrls: ['./assignment-form.component.css']
})
export class AssignmentFormComponent implements OnInit {
  form!: FormGroup;
  mode: 'create' | 'edit' = 'create';
  isSubmitting = false;
  employees: any[] = [];
  projects: any[] = [];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<AssignmentFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private employeeService: EmployeeService,
    private projectService: ProjectService
  ) {
    this.mode = data.mode;
  }

  ngOnInit(): void {
    this.loadEmployeesAndProjects();
    this.initForm();
  }

  private loadEmployeesAndProjects(): void {
    this.employeeService.getAll().subscribe(
      employees => this.employees = employees
    );
    this.projectService.getAll().subscribe(
      projects => this.projects = projects
    );
  }

  private initForm(): void {
    this.form = this.fb.group({
      employeeId: [
        this.mode === 'edit' ? this.data.assignment?.employeeId : '',
        [Validators.required]
      ],
      projectId: [
        this.mode === 'edit' ? this.data.assignment?.projectId : '',
        [Validators.required]
      ],
      startDate: [
        this.mode === 'edit' ? this.data.assignment?.startDate : '',
        [Validators.required]
      ],
      endDate: [
        this.mode === 'edit' ? this.data.assignment?.endDate : '',
        [Validators.required]
      ]
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.isSubmitting = true;
      this.dialogRef.close(this.form.value);
    }
  }

  getErrorMessage(fieldName: string): string {
    const control = this.form.get(fieldName);
    if (control?.hasError('required')) {
      return `${fieldName} est requis`;
    }
    return '';
  }
}
