import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, CreateEmployeeRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private apiUrl = `${environment.apiUrl}/employees`;
  private adminUrl = `${environment.apiUrl}/admin/employees`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.adminUrl);
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.adminUrl}/${id}`);
  }

  create(employee: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(this.adminUrl, employee);
  }

  update(id: number, employee: CreateEmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(this.adminUrl, { ...employee, id });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminUrl}/${id}`);
  }

  search(keyword: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(this.adminUrl);
  }

  updateOwnProfile(id: number, employee: CreateEmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/${id}/profile`, { ...employee, id });
  }
}
