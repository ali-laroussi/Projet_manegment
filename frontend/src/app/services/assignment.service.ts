import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Assignment, CreateAssignmentRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {
  private apiUrl = `${environment.apiUrl}/assignments`;
  private adminUrl = `${environment.apiUrl}/admin/assignments`;
  private projectsUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(this.apiUrl);
  }

  getById(id: number): Observable<Assignment> {
    return this.http.get<Assignment>(`${this.apiUrl}/${id}`);
  }

  getByEmployeeId(employeeId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.apiUrl}/employee/${employeeId}`);
  }

  getByProjectId(projectId: number): Observable<Assignment[]> {
    return this.http.get<Assignment[]>(`${this.apiUrl}/project/${projectId}`);
  }

  create(assignment: CreateAssignmentRequest): Observable<Assignment> {
    return this.http.post<Assignment>(this.apiUrl, assignment);
  }

  update(id: number, assignment: CreateAssignmentRequest): Observable<Assignment> {
    return this.http.put<Assignment>(this.apiUrl, { ...assignment, id });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminUrl}/${id}`);
  }

  assignEmployeeToProject(
    projectId: number,
    employeeId: number,
    startDate: string,
    endDate: string
  ): Observable<Assignment> {
    return this.http.post<Assignment>(`${this.projectsUrl}/${projectId}/assign`, {
      employeeId,
      startDate,
      endDate
    });
  }
}
