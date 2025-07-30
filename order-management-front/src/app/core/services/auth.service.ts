import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthResponse, RegisterRequest, LoginRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<AuthResponse> {
    const loginData = {
      email, // le backend attend "email" maintenant
      password
    };
    return this.http.post<AuthResponse>(`${this.apiUrl}/api/auth/login`, loginData)
      .pipe(
        tap(response => {
          console.log('🔍 DEBUG: Réponse complète du login:', response);
          console.log('🔍 DEBUG: response.seller:', response.seller);
          console.log('🔍 DEBUG: response.seller.isAdmin:', response.seller?.isAdmin);
          
          if (response.token) {
            localStorage.setItem('token', response.token);
          }
          
          // Vérifier différentes possibilités de propriété admin
          const isAdminVariant1 = response.seller?.isAdmin;
          const isAdminVariant2 = (response.seller as any)?.isAdmin;
          const isAdminVariant3 = (response.seller as any)?.getIsAdmin;
          
          console.log('🔍 DEBUG: isAdminVariant1 (isAdmin):', isAdminVariant1);
          console.log('🔍 DEBUG: isAdminVariant2 (as any isAdmin):', isAdminVariant2);
          console.log('🔍 DEBUG: isAdminVariant3 (getIsAdmin):', isAdminVariant3);
          
          if (response.seller && (isAdminVariant1 === true || isAdminVariant2 === true)) {
            console.log('✅ Utilisateur identifié comme ADMIN');
            localStorage.setItem('isAdmin', 'true');
          } else {
            console.log('❌ Utilisateur identifié comme NON-ADMIN');
            localStorage.setItem('isAdmin', 'false');
          }
        })
      );
  }

  loginClient(phoneNumber: string, password: string): Observable<AuthResponse> {
    const loginData = {
      email: phoneNumber, // Utiliser l'email maintenant
      password
    };
    return this.http.post<AuthResponse>(`${this.apiUrl}/api/auth/login-client`, loginData)
      .pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem('token', response.token);
          }
          // Les clients ne sont jamais admin
          localStorage.setItem('isAdmin', 'false');
        })
      );
  }

  register(registerData: RegisterRequest) {
    return this.http.post<any>(`${this.apiUrl}/api/auth/register-client`, registerData);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('isAdmin');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAdmin(): boolean {
    return localStorage.getItem('isAdmin') === 'true';
  }

  getCurrentUser(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/api/auth/me`);
  }

  updateCurrentUser(data: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/api/auth/me`, data);
  }
} 