import { Component, OnInit } from '@angular/core';
import { Category } from '../../models/business.model';
import { CategoryService } from '../../services/category.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { CategoryFormComponent } from './category-form/category-form.component';

@Component({
  selector: 'app-categories',
  templateUrl: './categories.component.html',
  styleUrls: ['./categories.component.css']
})
export class CategoriesComponent implements OnInit {
  categories: Category[] = [];
  displayedColumns: string[] = ['name', 'actions'];
  isLoading = false;
  showAllCategories = false;

  constructor(
    private categoryService: CategoryService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  openAddCategoryDialog(): void {
    const dialogRef = this.dialog.open(CategoryFormComponent, {
      width: '400px',
      data: { mode: 'create', category: null }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.createCategory(result);
      }
    });
  }

  openEditCategoryDialog(category: Category): void {
    const dialogRef = this.dialog.open(CategoryFormComponent, {
      width: '400px',
      data: { mode: 'edit', category: category }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.updateCategory(category.id, result);
      }
    });
  }

  deleteCategory(category: Category): void {
    if (!confirm(`Êtes-vous sûr de vouloir supprimer la catégorie "${category.name}" ?`)) {
      return;
    }

    this.isLoading = true;
    this.categoryService.delete(category.id).subscribe({
      next: () => {
        this.snackBar.open(`La catégorie "${category.name}" a été supprimée avec succès`, 'Fermer', { duration: 3000 });
        this.loadCategories();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la suppression:', error);
        this.snackBar.open('Erreur lors de la suppression de la catégorie', 'Fermer', { duration: 3000 });
      }
    });
  }

  private createCategory(categoryData: { name: string }): void {
    this.isLoading = true;
    this.categoryService.create(categoryData).subscribe({
      next: (newCategory) => {
        this.snackBar.open('Catégorie créée avec succès', 'Fermer', { duration: 3000 });
        this.loadCategories();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la création:', error);
        this.snackBar.open('Erreur lors de la création de la catégorie', 'Fermer', { duration: 3000 });
      }
    });
  }

  private updateCategory(id: number, categoryData: { name: string }): void {
    this.isLoading = true;
    this.categoryService.update(id, categoryData).subscribe({
      next: (updatedCategory) => {
        this.snackBar.open('Catégorie modifiée avec succès', 'Fermer', { duration: 3000 });
        this.loadCategories();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors de la modification:', error);
        this.snackBar.open('Erreur lors de la modification de la catégorie', 'Fermer', { duration: 3000 });
      }
    });
  }

  private loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getAll().subscribe({
      next: (categories) => {
        this.categories = categories;
        this.isLoading = false;
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erreur lors du chargement:', error);
        this.snackBar.open('Erreur lors du chargement des catégories', 'Fermer', { duration: 3000 });
      }
    });
  }

  toggleCategories(): void {
    this.showAllCategories = !this.showAllCategories;
  }

  getDisplayedCategories(): Category[] {
    return this.showAllCategories ? this.categories : this.categories.slice(0, 3);
  }
}

