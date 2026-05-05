import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const token = this.authService.getToken();

    if (token && !this.authService.isTokenExpired()) {
      // Vérifier les rôles requis si spécifiés
      const requiredRoles = route.data['roles'] as string[];
      if (requiredRoles && requiredRoles.length > 0) {
        const user = this.authService.getCurrentUser();
        if (user && requiredRoles.includes(user.role)) {
          return true;
        }
        // Utilisateur authentifié mais sans les bonnes permissions
        this.router.navigate(['/unauthorized']);
        return false;
      }
      return true;
    }

    // Utilisateur non authentifié
    this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }
}
