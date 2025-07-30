

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface Seller {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  isAdmin: boolean;
  isEnabled: boolean;
  isAccountNonExpired: boolean;
  isAccountNonLocked: boolean;
  isCredentialsNonExpired: boolean;
}

export interface AuthResponse {
  token: string;
  seller: Seller;
} 