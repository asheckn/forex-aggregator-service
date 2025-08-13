package com.wiremit.forex_aggregator_service.features.auth;
import com.wiremit.forex_aggregator_service.features.user.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "authorization")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    // Register a new user
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(
          @RequestBody RegisterRequest request
    ){
        return service.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/self")
    public ResponseEntity<?> getUserByToken(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(user);
    }

}
