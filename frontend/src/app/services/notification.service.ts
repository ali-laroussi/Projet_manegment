import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppNotification, CreateNotificationRequest } from '../models/business.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  send(data: CreateNotificationRequest): Observable<AppNotification> {
    return this.http.post<AppNotification>(`${this.api}/admin/notifications`, data);
  }

  getMine(): Observable<AppNotification[]> {
    return this.http.get<AppNotification[]>(`${this.api}/employees/me/notifications`);
  }
}