package com.example.demo.auth;

import com.example.demo.email.EmailService;
import com.example.demo.email.EmailTemplateName;
import com.example.demo.role.RoleRepository;
import com.example.demo.security.JwtService;
import com.example.demo.user.Token;
import com.example.demo.user.TokenRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    public void register(RegistrationRequest request) throws MessagingException {
         var userRole = roleRepository.findByName("USER")
                 // todo - better exception handling
                 .orElseThrow(() -> new IllegalStateException("Role USER was not initialized"));

         var user = User.builder()
                 .firstName(request.getFirstName())
                 .lastName(request.getLastName())
                 .email(request.getEmail())
                 .password(passwordEncoder.encode(request.getPassword()))
                 .accountLocked(false)
                 .enabled(false)
                 .roles(List.of(userRole))
                 .build();

         userRepository.save(user);
         sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        Token newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken.getToken(),
                "Account activation"
        );
    }

    private Token generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return token;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                       request.getEmail(),
                       request.getPassword()
                )
        );

        var claims = new HashMap<String,Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims,user);

        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
         Token savedToken = tokenRepository.findByToken(token)
                 // todo exception has to be defined
                 .orElseThrow(() -> new RuntimeException("Token not found"));

         if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
             sendValidationEmail(savedToken.getUser());
             throw new RuntimeException("Activation token has expired. A new token has been sent");
         }

         var user = userRepository.findById(savedToken.getUser().getId())
                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));

         user.setEnabled(true);
         userRepository.save(user);
         savedToken.setValiatedAt(LocalDateTime.now());
         tokenRepository.save(savedToken);
    }
}
