import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeDashboardComponent } from './employee-dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: EmployeeDashboardComponent
  }
];

@NgModule({
  declarations: [EmployeeDashboardComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatGridListModule,
    MatIconModule,
    MatButtonModule,
    RouterModule.forChild(routes)
  ]
})
export class DashboardModule { }
