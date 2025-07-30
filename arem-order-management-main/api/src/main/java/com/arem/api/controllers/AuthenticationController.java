package com.arem.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.arem.api.providers.AuthProvider;
import com.arem.api.security.AuthResponse;
import com.arem.api.security.JwtTokenUtil;
import com.arem.core.auth.AuthRequest;
import com.arem.core.auth.SellerDetails;
import com.arem.productInput.contracts.SellerContract;
import com.arem.core.model.Seller;
import com.arem.core.model.Customer;
import com.arem.dataservice.services.ISellerService;
import com.arem.dataservice.services.ICustomerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import com.arem.core.auth.CustomerDetails;

@RestController
@CrossOrigin
public class AuthenticationController
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ISellerService sellerService;
    
    @Autowired
    private ICustomerService customerService;

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest)
    {
        logger.info("Tentative de login pour email: '{}'", authRequest.getEmail());
        logger.info("Mot de passe reçu: '{}'", authRequest.getPassword());

        try
        {
            logger.debug("Received auth request: {}", authRequest);

            if (authRequest == null) {
                logger.error("Auth request is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Auth request cannot be null"));
            }

            logger.debug("Email: '{}', Password length: {}",
                authRequest.getEmail(),
                authRequest.getPassword() != null ? authRequest.getPassword().length() : 0);

            if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
                logger.error("Email or password is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email and password are required"));
            }

            if (authRequest.getEmail().trim().isEmpty() || authRequest.getPassword().trim().isEmpty()) {
                logger.error("Email or password is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email and password cannot be empty"));
            }

            authenticate(authRequest.getEmail(), authRequest.getPassword());

            final UserDetails userDetails = authProvider.loadUserByUsername(authRequest.getEmail());
            if (userDetails == null)
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not found"));
            }

            // Vérifier si c'est un Seller ou un Customer
            if (userDetails instanceof SellerDetails) {
                SellerDetails sellerDetails = (SellerDetails) userDetails;
                final String token = jwtTokenUtil.generateToken(sellerDetails);
                return ResponseEntity.ok(new AuthResponse(token, new SellerContract(sellerDetails.getSeller())));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid user type for this endpoint"));
            }
        }
        catch (DisabledException e)
        {
            logger.error("User account is disabled", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("User account is disabled"));
        }
        catch (BadCredentialsException e)
        {
            logger.error("Invalid credentials", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid credentials"));
        }
        catch (UsernameNotFoundException e)
        {
            logger.error("User not found: {}", authRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication failed: " + e.getMessage()));
        }
        catch (Exception e)
        {
            logger.error("Unexpected error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/api/auth/login-client")
    public ResponseEntity<?> createClientAuthenticationToken(@RequestBody AuthRequest authRequest)
    {
        logger.info("Tentative de login client pour email: '{}'", authRequest.getEmail());

        try
        {
            if (authRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Auth request cannot be null"));
            }

            if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email and password are required"));
            }

            if (authRequest.getEmail().trim().isEmpty() || authRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email and password cannot be empty"));
            }

            // Chercher le client par email directement dans le service
            Customer customer = customerService.getCustomerByEmail(authRequest.getEmail());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Client not found with this email"));
            }

            // Vérifier le mot de passe
            if (!passwordEncoder.matches(authRequest.getPassword(), customer.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid password"));
            }

            // Créer CustomerDetails directement
            com.arem.core.auth.CustomerDetails customerDetails = new com.arem.core.auth.CustomerDetails(customer);
            
            final String token = jwtTokenUtil.generateToken(customerDetails);

            return ResponseEntity.ok(new AuthResponse(token, customerDetails.getCustomer()));
        }
        catch (Exception e)
        {
            logger.error("Unexpected error during client authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping({"/api/auth/register", "/auth/register"})
    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
        try {
            if (authRequest == null || authRequest.getEmail() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Email and password are required"));
            }

            // Vérifier que l'utilisateur n'existe pas déjà
            try {
                if (authProvider.loadUserByUsername(authRequest.getEmail()) != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("User already exists"));
                }
            } catch (UsernameNotFoundException e) {
                // OK, l'utilisateur n'existe pas
            }

            Seller newSeller = new Seller();
            newSeller.setEmail(authRequest.getEmail());
            newSeller.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            newSeller.setIsAdmin(false); // Client par défaut

            // Champs hérités de User (tous obligatoires !)
            newSeller.setFirstName(authRequest.getFirstName());
            newSeller.setLastName(authRequest.getLastName());
            newSeller.setPhoneNumber(authRequest.getPhoneNumber());
            newSeller.setAddress(authRequest.getAddress());
            newSeller.setVersion(1);
            newSeller.setIsAccountNonExpired(true);
            newSeller.setIsAccountNonLocked(true);
            newSeller.setIsCredentialsNonExpired(true);
            newSeller.setIsEnabled(true);
            newSeller.setCreationDate(java.time.LocalDateTime.now());
            newSeller.setModifDate(java.time.LocalDateTime.now());

            sellerService.save(newSeller);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/api/auth/register-client")
    public ResponseEntity<?> registerClient(@RequestBody AuthRequest authRequest) {
        try {
            if (authRequest == null || authRequest.getFirstName() == null || authRequest.getLastName() == null || 
                authRequest.getPhoneNumber() == null || authRequest.getEmail() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("First name, last name, phone number, email and password are required"));
            }

            // Vérifier que le client n'existe pas déjà (par email)
            try {
                Customer existingCustomerByEmail = customerService.getCustomerByEmail(authRequest.getEmail());
                if (existingCustomerByEmail != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Client with this email already exists"));
                }
            } catch (Exception e) {
                // OK, le client n'existe pas
            }

            // Vérifier que le client n'existe pas déjà (par téléphone)
            try {
                Customer existingCustomerByPhone = customerService.getCustomerByPhoneNumber(authRequest.getPhoneNumber());
                if (existingCustomerByPhone != null) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ErrorResponse("Client with this phone number already exists"));
                }
            } catch (Exception e) {
                // OK, le client n'existe pas
            }

            // Créer un nouveau Customer
            Customer newCustomer = new Customer();
            newCustomer.setFirstName(authRequest.getFirstName());
            newCustomer.setLastName(authRequest.getLastName());
            newCustomer.setPhoneNumber(authRequest.getPhoneNumber());
            newCustomer.setEmail(authRequest.getEmail());
            newCustomer.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            newCustomer.setAddress(authRequest.getAddress() != null ? authRequest.getAddress() : "Adresse non spécifiée");
            newCustomer.setVersion(1);
            newCustomer.setCreationDate(java.time.LocalDateTime.now());
            newCustomer.setModifDate(java.time.LocalDateTime.now());

            customerService.save(newCustomer);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Client registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Client registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof SellerDetails) {
            SellerDetails userDetails = (SellerDetails) principal;
            return ResponseEntity.ok(new SellerContract(userDetails.getSeller()));
        } else if (principal instanceof CustomerDetails) {
            CustomerDetails customerDetails = (CustomerDetails) principal;
            Customer customer = customerDetails.getCustomer();
            return ResponseEntity.ok(Map.of(
                "id", customer.getId(),
                "firstName", customer.getFirstName(),
                "lastName", customer.getLastName(),
                "email", customer.getEmail(),
                "phoneNumber", customer.getPhoneNumber(),
                "address", customer.getAddress(),
                "role", "CUSTOMER"
            ));
        } else if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Pour les clients, on récupère les détails depuis la base de données
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
            try {
                Customer customer = customerService.getCustomerByEmail(user.getUsername());
                if (customer != null) {
                    return ResponseEntity.ok(Map.of(
                        "id", customer.getId(),
                        "firstName", customer.getFirstName(),
                        "lastName", customer.getLastName(),
                        "email", customer.getEmail(),
                        "phoneNumber", customer.getPhoneNumber(),
                        "address", customer.getAddress(),
                        "role", "CUSTOMER"
                    ));
                }
            } catch (Exception e) {
                logger.error("Error retrieving customer details: " + e.getMessage());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid user type"));
    }

    @PutMapping("/api/auth/me")
    public ResponseEntity<?> updateCurrentUser(
            Authentication authentication,
            @RequestBody SellerContract updatedSeller) {
        if (authentication == null || !(authentication.getPrincipal() instanceof SellerDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));
        }
        SellerDetails userDetails = (SellerDetails) authentication.getPrincipal();
        Seller seller = userDetails.getSeller();

        // Mets à jour les champs modifiables
        seller.setFirstName(updatedSeller.getFirstName());
        seller.setLastName(updatedSeller.getLastName());
        seller.setPhoneNumber(updatedSeller.getPhoneNumber());
        seller.setAddress(updatedSeller.getAddress());
        // (Ne modifie pas l'email ni le mot de passe ici pour la sécurité)

        try {
            sellerService.save(seller);
            return ResponseEntity.ok(new SellerContract(seller));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de la mise à jour du compte : " + e.getMessage()));
        }
    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
