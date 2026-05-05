export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: AuthUser;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  categoryId: number;
}

export interface AuthUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: 'ADMIN' | 'EMPLOYEE';
  categoryId: number;
}
