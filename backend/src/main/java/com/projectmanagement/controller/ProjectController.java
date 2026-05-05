package com.projectmanagement.controller;

import com.projectmanagement.dto.AssignmentDTO;
import com.projectmanagement.dto.ProjectDTO;
import com.projectmanagement.entity.Assignment;
import com.projectmanagement.entity.Project;
import com.projectmanagement.mapper.DtoMapper;
import com.projectmanagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DtoMapper mapper;

    public static class AssignRequest {
        public Long employeeId;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate endDate;
    }

    @GetMapping
    public List<ProjectDTO> list() {
        return projectService.findAll().stream().map(mapper::toProjectDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Project> p = projectService.findById(id);
        if (p.isPresent()) {
            return ResponseEntity.ok(mapper.toProjectDTO(p.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Project not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody ProjectDTO projectDto) {
        try {
            Project saved = projectService.save(mapper.toProject(projectDto));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toProjectDTO(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody ProjectDTO projectDto) {
        try {
            Project updated = projectService.update(mapper.toProject(projectDto));
            return ResponseEntity.ok(mapper.toProjectDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            projectService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/{projectId}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignEmployee(@PathVariable Long projectId, @RequestBody AssignRequest req) {
        try {
            Assignment a = projectService.addEmployeeToProject(projectId, req.employeeId, req.startDate, req.endDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAssignmentDTO(a));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> byEmployee(@PathVariable Long employeeId) {
        try {
            List<ProjectDTO> list = projectService.findByEmployee(employeeId).stream().map(mapper::toProjectDTO).collect(Collectors.toList());
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
