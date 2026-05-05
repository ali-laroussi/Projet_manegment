import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-category-form',
  templateUrl: './category-form.component.html',
  styleUrls: ['./category-form.component.css']
})
export class CategoryFormComponent implements OnInit {
  form!: FormGroup;
  mode: 'create' | 'edit' = 'create';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<CategoryFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.mode = data.mode;
  }

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]]
    });

    if (this.mode === 'edit' && this.data.category) {
      this.form.patchValue({
        name: this.data.category.name
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      return;
    }

    this.isSubmitting = true;
    const formData = {
      name: this.form.get('name')?.value
    };

    this.dialogRef.close(formData);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(): string {
    const field = this.form.get('name');
    if (field?.hasError('required')) {
      return 'Le nom de la catégorie est requis';
    }
    if (field?.hasError('minlength')) {
      return 'Le nom doit contenir au moins 2 caractères';
    }
    if (field?.hasError('maxlength')) {
      return 'Le nom ne doit pas dépasser 100 caractères';
    }
    return '';
  }
}
