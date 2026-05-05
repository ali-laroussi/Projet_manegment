import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.api}/admin/categories`);
  }

  getById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.api}/admin/categories/${id}`);
  }

  create(category: { name: string }): Observable<Category> {
    return this.http.post<Category>(`${this.api}/admin/categories`, category);
  }

  update(id: number, category: { name: string }): Observable<Category> {
    return this.http.put<Category>(`${this.api}/admin/categories/${id}`, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/categories/${id}`);
  }
}