package com.example.security.auth;

import com.example.security.config.JwtService;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public RegisterResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return RegisterResponse.builder()
                .message("Registered Successfully")
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var exp = jwtService.getExpirationToken(jwtToken);
        return AuthenticationResponse.builder()
                .exp(exp)
                .user_id(user.getId())
                .user_name((user.getUsername() != null) ? user.getUsername() : null)
                .company_id((user.getCompany() != null) ? user.getCompany().getId() : null)
                .company_name((user.getCompany() != null) ? user.getCompany().getName() : null)
                .channel_id((user.getChannel() != null) ? user.getChannel().getId() : null)
                .channel_name((user.getChannel() != null) ? user.getChannel().getName() : null)
                .sale_team_id((user.getTeam() != null) ? user.getTeam().getId() : null)
                .sale_team_name((user.getTeam() != null) ? user.getTeam().getName() : null)
                .token(jwtToken)
                .build();
    }
}
