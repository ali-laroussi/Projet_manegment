import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, RegisterRequest, AuthUser } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private employeeUrl = `${environment.apiUrl}/employees`;
  private tokenKey = environment.tokenKey;
  private userKey = environment.userKey;

  private currentUserSubject: BehaviorSubject<AuthUser | null>;
  public currentUser$: Observable<AuthUser | null>;

  private isAuthenticatedSubject: BehaviorSubject<boolean>;
  public isAuthenticated$: Observable<boolean>;

  constructor(private http: HttpClient, private router: Router) {
    const storedUser = localStorage.getItem(this.userKey);
    this.currentUserSubject = new BehaviorSubject<AuthUser | null>(
      storedUser ? JSON.parse(storedUser) : null
    );
    this.currentUser$ = this.currentUserSubject.asObservable();

    this.isAuthenticatedSubject = new BehaviorSubject<boolean>(!!this.getToken());
    this.isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response: LoginResponse) => {
        this.setToken(response.token);
        this.setCurrentUser(response.user);
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => new Error('Erreur de connexion'));
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.apiUrl}/register`, request).pipe(
      catchError((error) => {
        console.error('Register error:', error);
        return throwError(() => new Error('Erreur lors de l\'inscription'));
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
    this.isAuthenticatedSubject.next(true);
  }

  getCurrentUser(): AuthUser | null {
    return this.currentUserSubject.value;
  }

  setCurrentUser(user: AuthUser | null): void {
    if (user) {
      localStorage.setItem(this.userKey, JSON.stringify(user));
      this.currentUserSubject.next(user);
    } else {
      localStorage.removeItem(this.userKey);
      this.currentUserSubject.next(null);
    }
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'ADMIN';
  }

  isEmployee(): boolean {
    const user = this.getCurrentUser();
    return user?.role === 'EMPLOYEE';
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    const payload = this.decodeTokenPayload(token);
    if (!payload || !payload.exp) return true;

    const expirationDate = new Date(payload.exp * 1000);
    return new Date() >= expirationDate;
  }

  private decodeTokenPayload(token: string): any {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) return null;

      const decoded = atob(parts[1]);
      return JSON.parse(decoded);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  updateProfile(userId: number, profileData: any): Observable<any> {
    return this.http.put<any>(`${this.employeeUrl}/${userId}`, profileData);
  }
}
