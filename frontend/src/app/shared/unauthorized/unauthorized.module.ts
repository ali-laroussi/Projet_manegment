import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule, Routes } from '@angular/router';
import { UnauthorizedComponent } from './unauthorized.component';

const routes: Routes = [
  {
    path: '',
    component: UnauthorizedComponent
  }
];

@NgModule({
  declarations: [UnauthorizedComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    RouterModule.forChild(routes)
  ]
})
export class UnauthorizedModule { }
