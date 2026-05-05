import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CategoryService } from '../../../services/category.service';
import { Category, CreateEmployeeRequest } from '../../../models/business.model';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.css']
})
export class EmployeeFormComponent implements OnInit {
  form!: FormGroup;
  mode: 'create' | 'edit' = 'create';
  categories: Category[] = [];
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    private categoryService: CategoryService,
    public dialogRef: MatDialogRef<EmployeeFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.mode = data.mode;
  }

  ngOnInit(): void {
    this.initializeForm();
    this.loadCategories();
  }

  private initializeForm(): void {
    this.form = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', this.mode === 'create' ? Validators.required : Validators.nullValidator],
      categoryId: ['', Validators.required],
      role: ['EMPLOYEE', Validators.required]
    });

    if (this.mode === 'edit' && this.data.employee) {
      const employee = this.data.employee;
      this.form.patchValue({
        firstName: employee.firstName,
        lastName: employee.lastName,
        email: employee.email,
        categoryId: employee.categoryId,
        role: employee.role
      });
      // Le mot de passe n'est pas rempli en mode édition
      this.form.get('password')?.setValidators([]);
      this.form.get('password')?.updateValueAndValidity();
    }
  }

  private loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des catégories:', error);
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    this.isSubmitting = true;
    const formData: CreateEmployeeRequest = {
      firstName: this.form.get('firstName')?.value,
      lastName: this.form.get('lastName')?.value,
      email: this.form.get('email')?.value,
      password: this.form.get('password')?.value,
      categoryId: this.form.get('categoryId')?.value
    };

    this.dialogRef.close(formData);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(fieldName: string): string {
    const field = this.form.get(fieldName);
    if (field?.hasError('required')) {
      return `${fieldName} est requis`;
    }
    if (field?.hasError('minlength')) {
      return `${fieldName} doit contenir au moins 2 caractères`;
    }
    if (field?.hasError('email')) {
      return 'Email invalide';
    }
    return '';
  }
}
