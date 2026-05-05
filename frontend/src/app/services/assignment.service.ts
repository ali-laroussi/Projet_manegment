import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Assignment, CreateAssignmentRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.api}/assignments`);
  }

  getById(id: number): Observable<Assignment> {
    return this.http.get<Assignment>(`${this.api}/assignments/${id}`);
  }

  getByEmployeeId(employeeId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.api}/assignments/employee/${employeeId}`);
  }

  getByProjectId(projectId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.api}/assignments/project/${projectId}`);
  }

  create(data: CreateAssignmentRequest): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.api}/assignments`, data);
  }

  update(id: number, data: CreateAssignmentRequest): Observable<Assignment> {
    return this.http.put<Assignment>(`${this.api}/assignments/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/assignments/${id}`);
  }

  assignEmployeeToProject(projectId: number, payload: any): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.api}/projects/${projectId}/assign`, payload);
  }
}
