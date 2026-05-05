package com.projectmanagement.controller;

import com.projectmanagement.dto.LoginDTO;
import com.projectmanagement.dto.RegisterDTO;
import com.projectmanagement.dto.EmployeeResponseDTO;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.mapper.DtoMapper;
import com.projectmanagement.security.JwtTokenProvider;
import com.projectmanagement.service.AuthenticationService;
import com.projectmanagement.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO req) {
        Optional<Employee> user = authenticationService.authenticate(req.getEmail(), req.getPassword());
        if (user.isPresent()) {
            Employee emp = user.get();
            EmployeeResponseDTO dto = mapper.toEmployeeResponseDTO(emp);
            // build Authentication for token generation
            org.springframework.security.core.userdetails.UserDetails userDetails = customUserDetailsService.loadUserByUsername(emp.getEmail());
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            String token = jwtTokenProvider.generateToken(auth);
            return ResponseEntity.ok(Map.of("token", token, "user", dto));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO req) {
        try {
            Employee employee = new Employee();
            employee.setFirstName(req.getFirstName());
            employee.setLastName(req.getLastName());
            employee.setEmail(req.getEmail());
            employee.setPassword(req.getPassword());

            Employee registered = authenticationService.register(employee, req.getCategoryId());
            EmployeeResponseDTO dto = mapper.toEmployeeResponseDTO(registered);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
