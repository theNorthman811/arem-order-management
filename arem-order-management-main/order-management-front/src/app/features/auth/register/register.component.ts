import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    phoneNumber: ['', [Validators.required]],
    address: ['', [Validators.required]]
  });

  errorMessage: string = '';

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;
      const registerData: RegisterRequest = {
        email: formValue.email || '',
        password: formValue.password || '',
        firstName: formValue.firstName || '',
        lastName: formValue.lastName || '',
        phoneNumber: formValue.phoneNumber || '',
        address: formValue.address || ''
      };
      this.authService.register(registerData).subscribe(
        () => {
          // Auto-login après inscription réussie
          this.authService.loginClient(
            registerData.email, // Utiliser l'email maintenant
            registerData.password
          ).subscribe(
            () => {
              this.router.navigate(['/products']);
            },
            (loginError: any) => {
              this.errorMessage = 'Auto-login failed. Please try to login manually.';
              console.error('Auto-login failed:', loginError);
            }
          );
        },
        (error) => {
          // Gestion des erreurs d'inscription (ex: doublon téléphone)
          if (error.error && error.error.message && error.error.message.includes('UniquePhoneNumber')) {
            this.errorMessage = 'Ce numéro de téléphone est déjà utilisé.';
          } else if (error.error && error.error.message) {
            this.errorMessage = error.error.message;
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
          }
          console.error('Registration failed:', error);
        }
      );
    }
  }
} 