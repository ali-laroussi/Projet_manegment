package com.projectmanagement.controller;

import com.projectmanagement.dto.ChangePasswordDTO;
import com.projectmanagement.dto.EmployeeCreateDTO;
import com.projectmanagement.dto.EmployeeResponseDTO;
import com.projectmanagement.dto.UpdateProfileDTO;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Category;
import com.projectmanagement.mapper.DtoMapper;
import com.projectmanagement.security.CustomUserDetails;
import com.projectmanagement.service.EmployeeService;
import com.projectmanagement.service.NotificationService;
import com.projectmanagement.repository.EmployeeRepository;
import com.projectmanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DtoMapper mapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id) {
        Optional<Employee> emp = employeeService.findById(id);
        if (emp.isPresent()) {
            return ResponseEntity.ok(mapper.toEmployeeResponseDTO(emp.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Employee not found"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeCreateDTO dto) {
        try {
            if (dto.getId() != null && !id.equals(dto.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID mismatch"));
            }
            Employee employee = mapper.toEmployeeFromCreateDTO(dto);
            employee.setId(id);
            Employee updated = employeeService.update(employee);
            return ResponseEntity.ok(mapper.toEmployeeResponseDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/profile")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @Valid @RequestBody UpdateProfileDTO dto) {
        try {
            if (dto.getId() != null && !id.equals(dto.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "ID mismatch"));
            }
            
            // Charger l'entité existante AVANT de la modifier
            Optional<Employee> existingOpt = employeeRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'employé n'existe pas"));
            }
            
            Employee employee = existingOpt.get();
            
            // Mettre à jour SEULEMENT les champs autorisés
            employee.setFirstName(dto.getFirstName());
            employee.setLastName(dto.getLastName());
            employee.setEmail(dto.getEmail());
            
            // Mettre à jour la catégorie si fournie
            if (dto.getCategoryId() != null) {
                Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
                if (category.isPresent()) {
                    employee.setCategory(category.get());
                }
            }
            
            // Mettre à jour le mot de passe seulement s'il est fourni
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                employee.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            
            // NE PAS modifier le rôle - il est préservé
            Employee updated = employeeService.update(employee);
            return ResponseEntity.ok(mapper.toEmployeeResponseDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordDTO req) {
        try {
            boolean ok = employeeService.changePassword(id, req.getOldPassword(), req.getNewPassword());
            return ok ? ResponseEntity.ok(Map.of("status", "password-changed")) : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me/notifications")
    public ResponseEntity<?> getMyNotifications(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthenticated"));
        }

        return ResponseEntity.ok(
            notificationService.findByEmployee(currentUser.getId()).stream()
                .map(mapper::toNotificationDTO)
                .collect(java.util.stream.Collectors.toList())
        );
    }
}
