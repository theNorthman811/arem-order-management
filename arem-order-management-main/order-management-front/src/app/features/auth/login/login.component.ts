import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">Login</div>
            <div class="card-body">
              <div *ngIf="successMessage" class="alert alert-success">
                {{ successMessage }}
              </div>
              <div *ngIf="errorMessage" class="alert alert-danger">
                {{ errorMessage }}
              </div>
              <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input type="email" class="form-control" formControlName="email">
                  <div *ngIf="loginForm.get('email')?.touched && loginForm.get('email')?.invalid" class="text-danger">
                    <small *ngIf="loginForm.get('email')?.errors?.['required']">Email is required</small>
                    <small *ngIf="loginForm.get('email')?.errors?.['email']">Invalid email format</small>
                  </div>
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input type="password" class="form-control" formControlName="password">
                  <div *ngIf="loginForm.get('password')?.touched && loginForm.get('password')?.invalid" class="text-danger">
                    <small *ngIf="loginForm.get('password')?.errors?.['required']">Password is required</small>
                  </div>
                </div>
                <button type="submit" class="btn btn-primary" [disabled]="loginForm.invalid || isLoading">
                  {{ isLoading ? 'Logging in...' : 'Login' }}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/orders']);
    }
    
    // Vérifier s'il y a un message de succès d'inscription
    this.route.queryParams.subscribe(params => {
      if (params['message']) {
        this.successMessage = params['message'];
      }
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      const { email, password } = this.loginForm.value;
      
      console.log('🔍 Tentative de connexion avec:', email);
      
      // D'abord essayer la connexion ADMIN/SELLER
      this.authService.login(email, password).subscribe({
        next: () => {
          console.log('✅ Connexion ADMIN réussie');
          if (this.authService.isAdmin()) {
            this.router.navigate(['/admin/dashboard']);
          } else {
            // Seller non-admin
            this.router.navigate(['/products']);
          }
        },
        error: (adminError) => {
          console.log('❌ Connexion admin échouée, essai client...', adminError);
          
          // Si la connexion admin échoue, essayer la connexion CLIENT
          this.authService.loginClient(email, password).subscribe({
            next: () => {
              console.log('✅ Connexion CLIENT réussie');
              this.router.navigate(['/products']);
            },
            error: (clientError) => {
              console.error('❌ Connexion client échouée aussi:', clientError);
              this.isLoading = false;
              if (clientError.status === 401) {
                this.errorMessage = 'Email ou mot de passe invalide';
              } else if (clientError.error?.message) {
                this.errorMessage = clientError.error.message;
              } else {
                this.errorMessage = 'Une erreur est survenue lors de la connexion. Veuillez réessayer.';
              }
            }
          });
        }
      });
    }
  }
} 