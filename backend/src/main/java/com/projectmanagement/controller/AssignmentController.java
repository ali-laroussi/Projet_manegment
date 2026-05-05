package com.projectmanagement.controller;

import com.projectmanagement.dto.AssignmentDTO;
import com.projectmanagement.entity.Assignment;
import com.projectmanagement.mapper.DtoMapper;
import com.projectmanagement.service.AssignmentService;
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
@RequestMapping("/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private DtoMapper mapper;

    public static class DateRangeReq {
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        public LocalDate endDate;
    }

    @GetMapping
    public List<AssignmentDTO> list() {
        return assignmentService.findAll().stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Assignment> a = assignmentService.findById(id);
        if (a.isPresent()) {
            return ResponseEntity.ok(mapper.toAssignmentDTO(a.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Assignment not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody AssignmentDTO dto) {
        try {
            Assignment toSave = mapper.toAssignment(dto);
            Assignment saved = assignmentService.save(toSave);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toAssignmentDTO(saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@Valid @RequestBody AssignmentDTO dto) {
        try {
            Assignment toUpdate = mapper.toAssignment(dto);
            Assignment updated = assignmentService.update(toUpdate);
            return ResponseEntity.ok(mapper.toAssignmentDTO(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            assignmentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> byEmployee(@PathVariable Long employeeId) {
        try {
            List<AssignmentDTO> list = assignmentService.findByEmployee(employeeId).stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> byProject(@PathVariable Long projectId) {
        try {
            List<AssignmentDTO> list = assignmentService.findByProject(projectId).stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/conflicts")
    public ResponseEntity<?> conflicts(@RequestParam Long employeeId,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        try {
            List<AssignmentDTO> conflicts = assignmentService.detectConflicts(employeeId, start, end).stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
            return ResponseEntity.ok(conflicts);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/ending-in/{days}")
    public List<AssignmentDTO> endingIn(@PathVariable int days) {
        return assignmentService.findAssignmentsEndingIn(days).stream().map(mapper::toAssignmentDTO).collect(Collectors.toList());
    }
}
