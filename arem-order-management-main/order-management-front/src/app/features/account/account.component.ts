import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Customer } from '../../core/models/customer.model';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  userInfo: any = null;
  loading = true;
  error: string | null = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.loading = true;
    this.error = null;

    this.authService.getCurrentUser().subscribe({
      next: (userInfo) => {
        console.log('User info received:', userInfo);
        this.userInfo = userInfo;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading user info:', error);
        this.error = 'Erreur lors du chargement des informations du compte';
        this.loading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    // Redirection sera gérée par le guard
  }
}
