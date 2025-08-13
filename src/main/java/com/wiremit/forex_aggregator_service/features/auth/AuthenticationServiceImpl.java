package com.wiremit.forex_aggregator_service.features.auth;

import com.wiremit.forex_aggregator_service.config.JwtService;
import com.wiremit.forex_aggregator_service.features.user.Role;
import com.wiremit.forex_aggregator_service.features.user.User;
import com.wiremit.forex_aggregator_service.features.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<?> register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email is already in use"));
        }

        // Map request to entity
        // Map request to entity using setters / constructor
        User user = new User();

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setPassword(passwordEncoder.encode(request.password()));

// Default role assignment
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_CLIENT);
        user.setRoles(roles);

// Set defaults for active / deleted
        user.setIsActive(true);
        user.setIsDeleted(false);

//        try {
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered successfully"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred"));
//        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = userRepository.findByEmail(request.getEmail());


        if (user.isPresent()) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var jwtToken = jwtService.generateToken(user.get());

            return AuthenticationResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .data(user)
                    .build();
        }else {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect username or password");
        }
    }

}
