package com.example.pfa_uplaod.modal;

import com.example.pfa_uplaod.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Admin user created!");
        }
    }
}

