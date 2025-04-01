package com.example.pfa_uplaod.controller;

import com.example.pfa_uplaod.Dto.AuthRequest;
import com.example.pfa_uplaod.Dto.AuthResponse;
import com.example.pfa_uplaod.modal.UserEntity;
import com.example.pfa_uplaod.repository.UserRepository;
import com.example.pfa_uplaod.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            String token = jwtService.generateToken(userDetails);
            // Récupérer le rôle de l'utilisateur (ici on prend le premier rôle trouvé)
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_USER");

            return ResponseEntity.ok(new AuthResponse(token,role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserEntity signUpRequest) {

        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }

        // Encryptage du mot de passe
        String encryptedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        // Définir un rôle par défaut si non fourni
        String role = (signUpRequest.getRole() != null) ? signUpRequest.getRole() : "USER";

        // Créer et enregistrer le nouvel utilisateur
        UserEntity newUser = new UserEntity(signUpRequest.getUsername(), encryptedPassword, role);
        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

}