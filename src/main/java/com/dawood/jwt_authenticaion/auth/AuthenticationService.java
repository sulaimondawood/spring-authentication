package com.dawood.jwt_authenticaion.auth;

import com.dawood.jwt_authenticaion.config.JwtService;
import com.dawood.jwt_authenticaion.user.Role;
import com.dawood.jwt_authenticaion.user.User;
import com.dawood.jwt_authenticaion.user.UserRepository;
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

  public AuthenticationResponse register(RegisterRequest request){
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .role(Role.USER)
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
    userRepository.save(user);

    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(jwtToken)
        .build();
  }

  public AuthenticationResponse login(LoginRequest request){
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = userRepository.findByEmail(request.getEmail())
        .orElseThrow();
    var token = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }
}
