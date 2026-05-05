package com.projectmanagement.controller;

import com.projectmanagement.dto.AssignmentDTO;
import com.projectmanagement.dto.CategoryDTO;
import com.projectmanagement.dto.EmployeeCreateDTO;
import com.projectmanagement.dto.EmployeeResponseDTO;
import com.projectmanagement.dto.NotificationCreateDTO;
import com.projectmanagement.dto.NotificationDTO;
import com.projectmanagement.dto.ProjectDTO;
import com.projectmanagement.entity.Assignment;
import com.projectmanagement.entity.Category;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Notification;
import com.projectmanagement.entity.Project;
import com.projectmanagement.mapper.DtoMapper;
import com.projectmanagement.service.AssignmentService;
import com.projectmanagement.service.CategoryService;
import com.projectmanagement.service.EmployeeService;
import com.projectmanagement.service.NotificationService;
import com.projectmanagement.service.ProjectService;
import com.projectmanagement.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DtoMapper mapper;

    // ---------- Categories ----------
    @GetMapping("/categories")
    public List<CategoryDTO> listCategories() {
        return categoryService.findAll().stream().map(mapper::toCategoryDTO).collect(Collectors.toList());
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDto) {
        try {
            Category saved = categoryService.save(mapper.toCategory(categoryDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toCategoryDTO(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/categories")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody CategoryDTO categoryDto) {
        try {
            Category updated = categoryService.update(mapper.toCategory(categoryDto));
            return ResponseEntity.ok(mapper.toCategoryDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    // ---------- Employees ----------
    @GetMapping("/employees")
    public List<EmployeeResponseDTO> listEmployees() {
        return employeeService.findAll().stream().map(mapper::toEmployeeResponseDTO).collect(Collectors.toList());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id) {
        Optional<Employee> emp = employeeService.findById(id);
        if (emp.isPresent()) {
            return ResponseEntity.ok(mapper.toEmployeeResponseDTO(emp.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Employee not found"));
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
        try {
            Employee toSave = mapper.toEmployeeFromCreateDTO(dto);
            Employee saved = employeeService.save(toSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toEmployeeResponseDTO(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/employees")
    public ResponseEntity<?> updateEmployee(@Valid @RequestBody EmployeeCreateDTO dto) {
        try {
            Employee toUpdate = mapper.toEmployeeFromCreateDTO(dto);
            Employee updated = employeeService.update(toUpdate);
            return ResponseEntity.ok(mapper.toEmployeeResponseDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            employeeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    // ---------- Projects ----------
    @GetMapping("/projects")
    public List<ProjectDTO> listProjects() {
        return projectService.findAll().stream().map(mapper::toProjectDTO).collect(Collectors.toList());
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id) {
        Optional<Project> p = projectService.findById(id);
        if (p.isPresent()) {
            return ResponseEntity.ok(mapper.toProjectDTO(p.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Project not found"));
    }

    @PostMapping("/projects")
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectDTO projectDto) {
        try {
            Project saved = projectService.save(mapper.toProject(projectDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProjectDTO(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/projects")
    public ResponseEntity<?> updateProject(@Valid @RequestBody ProjectDTO projectDto) {
        try {
            Project updated = projectService.update(mapper.toProject(projectDto));
            return ResponseEntity.ok(mapper.toProjectDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    // ---------- Assignments ----------
    @GetMapping("/assignments")
    public List<AssignmentDTO> listAssignments() {
        return assignmentService.findAll().stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
    }

    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            assignmentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    // ---------- Notifications ----------
    @PostMapping("/notifications")
    public ResponseEntity<?> sendNotification(
            @Valid @RequestBody NotificationCreateDTO dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            String senderName = currentUser != null ? currentUser.getUsername() : "Admin";
            Notification notification = notificationService.sendToEmployee(dto.getEmployeeId(), dto.getMessage(), senderName);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toNotificationDTO(notification));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
