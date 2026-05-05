import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  loading = false;
  submitted = false;
  hidePassword = true;
  returnUrl: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Rediriger si déjà connecté
    if (this.authService.getToken() && !this.authService.isTokenExpired()) {
      const user = this.authService.getCurrentUser();
      if (user?.role === 'ADMIN') {
        this.router.navigate(['/admin/dashboard']);
      } else {
        this.router.navigate(['/employee/dashboard']);
      }
      return;
    }

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '';
    this.initializeForm();
  }

  private initializeForm(): void {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get f() {
    return this.loginForm.controls;
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  onSubmit(): void {
    this.submitted = true;

    // Arrêter si le formulaire est invalide
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    const loginRequest = {
      email: this.f['email'].value,
      password: this.f['password'].value
    };

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        const user = this.authService.getCurrentUser();
        this.snackBar.open('Connexion réussie!', 'Fermer', { duration: 3000 });
        
        // Redirection selon le rôle
        if (user?.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.router.navigate(['/employee/dashboard']);
        }
      },
      error: (error) => {
        this.loading = false;
        this.snackBar.open('Erreur: Email ou mot de passe incorrect', 'Fermer', { duration: 5000 });
        console.error('Login error:', error);
      }
    });
  }
}
