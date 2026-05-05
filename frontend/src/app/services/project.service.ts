import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project, CreateProjectRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;
  private adminUrl = `${environment.apiUrl}/admin/projects`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Project[]> {
    return this.http.get<Project[]>(this.adminUrl);
  }

  getById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.adminUrl}/${id}`);
  }

  create(project: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.adminUrl, project);
  }

  update(id: number, project: CreateProjectRequest): Observable<Project> {
    return this.http.put<Project>(this.adminUrl, { ...project, id });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminUrl}/${id}`);
  }

  search(keyword: string): Observable<Project[]> {
    return this.http.get<Project[]>(this.adminUrl);
  }

  getEmployeeProjects(employeeId: number): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/employee/${employeeId}`);
  }
}
