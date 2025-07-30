import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  accountForm: FormGroup;
  isEditing = false;
  user: any;
  successMessage = '';
  errorMessage = '';

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.accountForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: [{ value: '', disabled: true }, [Validators.required, Validators.email]],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user: any) => {
        this.user = user;
        this.accountForm.patchValue({
          firstName: user.firstName,
          lastName: user.lastName,
          email: user.email,
          phoneNumber: user.phoneNumber,
          address: user.address
        });
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les informations.';
      }
    });
  }

  enableEdit(): void {
    this.isEditing = true;
    this.accountForm.get('email')?.disable();
  }

  save(): void {
    if (this.accountForm.valid) {
      const updated = { ...this.user, ...this.accountForm.getRawValue() };
      this.authService.updateCurrentUser(updated).subscribe({
        next: () => {
          this.successMessage = 'Informations mises à jour !';
          this.isEditing = false;
          this.loadUser();
        },
        error: () => {
          this.errorMessage = 'Erreur lors de la mise à jour.';
        }
      });
    }
  }
} 