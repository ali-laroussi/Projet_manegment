import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const token = this.authService.getToken();

    if (token && !this.authService.isTokenExpired()) {
      const user = this.authService.getCurrentUser();
      if (user && user.role === 'ADMIN') {
        return true;
      }
      // Utilisateur authentifié mais pas admin
      this.router.navigate(['/unauthorized']);
      return false;
    }

    // Utilisateur non authentifié
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
