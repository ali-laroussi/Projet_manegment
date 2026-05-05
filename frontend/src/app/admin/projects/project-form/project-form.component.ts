import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-project-form',
  templateUrl: './project-form.component.html',
  styleUrls: ['./project-form.component.css']
})
export class ProjectFormComponent implements OnInit {
  form!: FormGroup;
  mode: 'create' | 'edit' = 'create';
  isSubmitting = false;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ProjectFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.mode = data.mode;
  }

  ngOnInit(): void {
    this.initForm();
  }

  private initForm(): void {
    this.form = this.fb.group({
      title: [
        this.mode === 'edit' ? this.data.project?.title : '',
        [Validators.required, Validators.minLength(3), Validators.maxLength(100)]
      ],
      description: [
        this.mode === 'edit' ? this.data.project?.description : '',
        [Validators.required, Validators.minLength(10), Validators.maxLength(500)]
      ],
      startDate: [
        this.mode === 'edit' ? this.data.project?.startDate : '',
        [Validators.required]
      ],
      endDate: [
        this.mode === 'edit' ? this.data.project?.endDate : '',
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
    if (control?.hasError('minlength')) {
      const minLength = control.getError('minlength')?.requiredLength;
      return `${fieldName} doit contenir au moins ${minLength} caractères`;
    }
    if (control?.hasError('maxlength')) {
      const maxLength = control.getError('maxlength')?.requiredLength;
      return `${fieldName} ne doit pas dépasser ${maxLength} caractères`;
    }
    return '';
  }
}
