import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';
import { MatSidenav } from '@angular/material/sidenav';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { AuthUser } from './models/auth.model';
import { EditProfileDialogComponent } from './shared/edit-profile-dialog/edit-profile-dialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  @ViewChild('sidenav') sidenav!: MatSidenav;
  
  title = 'Project Management Application';
  currentUser$: Observable<AuthUser | null>;
  isAuthenticated$: Observable<boolean>;
  isMobileView = false;

  constructor(private authService: AuthService, private router: Router, private dialog: MatDialog) {
    this.currentUser$ = this.authService.currentUser$;
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }

  ngOnInit(): void {
    this.checkMobileView();
    window.addEventListener('resize', () => this.checkMobileView());
  }

  checkMobileView(): void {
    this.isMobileView = window.innerWidth < 768;
  }

  logout(): void {
    this.authService.logout();
  }

  openEditProfileDialog(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.dialog.open(EditProfileDialogComponent, {
        width: '600px',
        maxHeight: '90vh',
        data: currentUser,
        panelClass: 'edit-profile-dialog',
        disableClose: false
      });
    }
  }

  closeSidenavIfMobile(): void {
    if (this.isMobileView) {
      this.sidenav.close();
    }
  }
}
