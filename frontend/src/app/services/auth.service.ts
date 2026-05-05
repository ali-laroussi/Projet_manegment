import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { LoginRequest, LoginResponse, RegisterRequest, AuthUser } from '../models/auth.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private api = environment.apiUrl;

  private currentUserSubject = new BehaviorSubject<AuthUser | null>(this.getStoredUser());
  currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(!!this.getToken());
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/auth/login`, request).pipe(
      tap(res => {
        localStorage.setItem(environment.tokenKey, res.token);
        localStorage.setItem(environment.userKey, JSON.stringify(res.user));
        this.currentUserSubject.next(res.user);
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthUser> {
    return this.http.post<AuthUser>(`${this.api}/auth/register`, request);
  }

  logout(): void {
    localStorage.clear();
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(environment.tokenKey);
  }

  getStoredUser(): AuthUser | null {
    const user = localStorage.getItem(environment.userKey);
    return user ? JSON.parse(user) : null;
  }

  getCurrentUser(): AuthUser | null {
    return this.currentUserSubject.value;
  }

  isAdmin(): boolean {
    return this.getCurrentUser()?.role === 'ADMIN';
  }

  isEmployee(): boolean {
    return this.getCurrentUser()?.role === 'EMPLOYEE';
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() >= payload.exp * 1000;
    } catch {
      return true;
    }
  }

  updateProfile(userId: number, data: any): Observable<any> {
    return this.http.put(`${this.api}/employees/${userId}`, data);
  }
  setCurrentUser(user: any): void {
  localStorage.setItem(environment.userKey, JSON.stringify(user));
  this.currentUserSubject.next(user);
}
}