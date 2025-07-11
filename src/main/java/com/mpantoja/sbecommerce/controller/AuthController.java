package com.mpantoja.sbecommerce.controller;


import com.mpantoja.sbecommerce.model.AppRole;
import com.mpantoja.sbecommerce.model.Role;
import com.mpantoja.sbecommerce.model.User;
import com.mpantoja.sbecommerce.repositories.RoleRepository;
import com.mpantoja.sbecommerce.repositories.UserRepository;
import com.mpantoja.sbecommerce.security.jwt.JwtUtils;
import com.mpantoja.sbecommerce.security.request.LoginRequest;
import com.mpantoja.sbecommerce.security.request.SignUpRequest;
import com.mpantoja.sbecommerce.security.response.MessageResponse;
import com.mpantoja.sbecommerce.security.response.UserInfoResponse;
import com.mpantoja.sbecommerce.security.services.UserDetailsImpl;
import com.mpantoja.sbecommerce.security.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles, jwtCookie);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: this username cannot be used"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: this email is already registered"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles==null) {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                    () -> new RuntimeException("Error: Role does not exist"));
            roles.add(userRole);
        } else {
            //admin--> ROLE_ADMIN
            //seller--> ROLE_SELLER

            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(
                                () -> new RuntimeException("Error: Role does not exist"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(
                                () -> new RuntimeException("Error: Role does not exist"));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(
                                () -> new RuntimeException("Error: Role does not exist"));
                        roles.add(userRole);
                }
            });
            user.setRoles(roles);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User Registered Successfully"));
        }


        return null;
    }


    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        if (authentication!=null) {
            return authentication.getName();
        } else {
            return "";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);
        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/signout")
    public ResponseEntity<?> signoutUser(){
        ResponseCookie cookie=jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("User Sign out successful"));
    }




}
