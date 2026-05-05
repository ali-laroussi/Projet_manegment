
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Employee, CreateEmployeeRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.api}/admin/employees`);
  }

  getById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.api}/admin/employees/${id}`);
  }

  create(data: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(`${this.api}/admin/employees`, data);
  }

  update(id: number, data: CreateEmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.api}/admin/employees/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/admin/employees/${id}`);
  }

  search(keyword: string): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.api}/admin/employees?search=${keyword}`);
  }

  updateOwnProfile(id: number, data: CreateEmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.api}/employees/${id}/profile`, data);
  }
}