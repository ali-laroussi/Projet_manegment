import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AuthService } from '../../services/auth.service';
import { EmployeeService } from '../../services/employee.service';
import { AuthUser } from '../../models/auth.model';

@Component({
  selector: 'app-edit-profile-dialog',
  templateUrl: './edit-profile-dialog.component.html',
  styleUrls: ['./edit-profile-dialog.component.css']
})
export class EditProfileDialogComponent implements OnInit {
  profileForm!: FormGroup;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  hidePassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<EditProfileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AuthUser
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    this.profileForm = this.formBuilder.group({
      firstName: [this.data.firstName, [Validators.required, Validators.minLength(2)]],
      lastName: [this.data.lastName, [Validators.required, Validators.minLength(2)]],
      email: [this.data.email, [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(6)]],
      confirmPassword: ['']
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(form: FormGroup): { [key: string]: boolean } | null {
    const password = form.get('password')?.value;
    const confirmPassword = form.get('confirmPassword')?.value;

    if (password && confirmPassword && password !== confirmPassword) {
      return { 'passwordMismatch': true };
    }
    return null;
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.errorMessage = 'Veuillez vérifier tous les champs';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formData = this.profileForm.value;
    const updateRequest = {
      id: this.data.id,
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password || undefined,
      categoryId: this.data.categoryId
    };

    this.employeeService.updateOwnProfile(this.data.id, updateRequest)
      .subscribe({
        next: (updatedEmployee) => {
          // Mettre à jour le profil dans le service d'authentification
          const updatedUser: AuthUser = {
            id: updatedEmployee.id,
            firstName: updatedEmployee.firstName,
            lastName: updatedEmployee.lastName,
            email: updatedEmployee.email,
            role: this.data.role,
            categoryId: updatedEmployee.categoryId
          };
          this.authService.setCurrentUser(updatedUser);

          this.successMessage = 'Profil mis à jour avec succès!';
          this.isLoading = false;

          setTimeout(() => {
            this.dialogRef.close(true);
          }, 1500);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.error || 'Erreur lors de la mise à jour du profil';
          console.error('Profile update error:', error);
        }
      });
  }

  closeDialog(): void {
    this.dialogRef.close(false);
  }

  hasError(fieldName: string, errorType: string): boolean {
    const field = this.profileForm.get(fieldName);
    return !!(field && field.hasError(errorType) && (field.dirty || field.touched));
  }

  getErrorMessage(fieldName: string): string {
    const field = this.profileForm.get(fieldName);
    if (!field) return '';

    if (field.hasError('required')) {
      return `${this.getFieldLabel(fieldName)} est requis`;
    }
    if (field.hasError('minlength')) {
      const minLength = field.getError('minlength')?.requiredLength;
      return `${this.getFieldLabel(fieldName)} doit contenir au moins ${minLength} caractères`;
    }
    if (field.hasError('email')) {
      return 'Veuillez entrer une adresse email valide';
    }
    return '';
  }

  getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'firstName': 'Prénom',
      'lastName': 'Nom',
      'email': 'Email',
      'password': 'Mot de passe',
      'confirmPassword': 'Confirmation du mot de passe'
    };
    return labels[fieldName] || fieldName;
  }
}
