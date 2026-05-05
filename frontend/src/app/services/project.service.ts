import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, CreateProjectRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.api}/admin/projects`);
  }

  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.api}/admin/projects/${id}`);
  }

  create(data: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(`${this.api}/admin/projects`, data);
  }

  update(id: number, data: CreateProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.api}/admin/projects/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/projects/${id}`);
  }

  search(keyword: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.api}/admin/projects?search=${keyword}`);
  }

  getEmployeeProjects(employeeId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.api}/projects/employee/${employeeId}`);
  }
}