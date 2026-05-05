import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppNotification, CreateNotificationRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private adminUrl = `${environment.apiUrl}/admin/notifications`;
  private employeeUrl = `${environment.apiUrl}/employees/me/notifications`;

  constructor(private http: HttpClient) {}

  send(request: CreateNotificationRequest): Observable<AppNotification> {
    return this.http.post<AppNotification>(this.adminUrl, request);
  }

  getMine(): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(this.employeeUrl);
  }
}
