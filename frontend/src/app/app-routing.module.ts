import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { AdminGuard } from './guards/admin.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'admin',
    canActivate: [AdminGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./admin/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'employees',
        loadChildren: () => import('./admin/employees/employees.module').then(m => m.EmployeesModule)
      },
      {
        path: 'categories',
        loadChildren: () => import('./admin/categories/categories.module').then(m => m.CategoriesModule)
      },
      {
        path: 'projects',
        loadChildren: () => import('./admin/projects/projects.module').then(m => m.ProjectsModule)
      },
      {
        path: 'assignments',
        loadChildren: () => import('./admin/assignments/assignments.module').then(m => m.AssignmentsModule)
      }
    ]
  },
  {
    path: 'employee',
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./employee/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'my-projects',
        loadChildren: () => import('./employee/my-projects/my-projects.module').then(m => m.MyProjectsModule)
      }
    ]
  },
  {
    path: 'unauthorized',
    loadChildren: () => import('./shared/unauthorized/unauthorized.module').then(m => m.UnauthorizedModule)
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { enableTracing: false })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
