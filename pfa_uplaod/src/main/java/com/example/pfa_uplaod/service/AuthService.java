package com.example.pfa_uplaod.service;

import com.example.pfa_uplaod.Dto.AuthRequest;
import com.example.pfa_uplaod.Dto.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

//Cette classe centralise la logique d’authentification et de génération de JWT.
/**AuthenticationManager
C’est le composant Spring Security qui prend en charge la validation des identifiants (username+password).

UserDetailsService
C’est l’interface Spring Security pour charger un utilisateur à partir d’un nom d’utilisateur (typiquement depuis la base de données).

JwtService
Votre propre service pour créer et (éventuellement) valider des JWT. */


@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

  //C’est le composant Spring Security qui prend en charge la validation des identifiants
  /**UserDetailsService
C’est l’interface Spring Security pour charger un utilisateur à partir d’un nom 
d’utilisateur (typiquement depuis la base de données). */

//L’objet AuthRequest est un simple DTO (Data Transfer Object) 
    public AuthResponse authenticateUser(AuthRequest authRequest) {
        // Authenticate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String token = jwtService.generateToken(userDetails);

        // Extract role
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        return new AuthResponse(token, role);
    }
}
