export interface Category {
  id: number;
  name: string;
}

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'EMPLOYEE';
  categoryId: number;
  categoryName?: string;
}

export interface Project {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
}

export interface Assignment {
  id: number;
  employeeId: number;
  projectId: number;
  startDate: string;
  endDate: string;
  employeeName?: string;
  projectTitle?: string;
}

export interface AppNotification {
  id: number;
  recipientId: number;
  recipientName?: string;
  message: string;
  senderName: string;
  read: boolean;
  createdAt: string;
}

export interface CreateEmployeeRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  categoryId: number;
}

export interface CreateProjectRequest {
  title: string;
  description: string;
  startDate: string;
  endDate: string;
}

export interface CreateAssignmentRequest {
  employeeId: number;
  projectId: number;
  startDate: string;
  endDate: string;
}

export interface CreateNotificationRequest {
  employeeId: number;
  message: string;
}
