package com.example.pfa_uplaod.service;
import com.example.pfa_uplaod.modal.UserEntity;
import com.example.pfa_uplaod.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // If they stored "ADMIN" or "ROLE_ADMIN", ensure exactly one "ROLE_" prefix
        String granted = user.getRole().toUpperCase();
        if (!granted.startsWith("ROLE_")) {
            granted = "ROLE_" + granted;
        }


        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(granted));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
