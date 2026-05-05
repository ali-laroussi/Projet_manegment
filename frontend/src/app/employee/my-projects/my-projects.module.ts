import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { MyProjectsComponent } from './my-projects.component';

const routes: Routes = [
  {
    path: '',
    component: MyProjectsComponent
  }
];

@NgModule({
  declarations: [MyProjectsComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatTableModule,
    MatIconModule,
    MatButtonModule,
    MatTabsModule,
    RouterModule.forChild(routes)
  ]
})
export class MyProjectsModule { }
