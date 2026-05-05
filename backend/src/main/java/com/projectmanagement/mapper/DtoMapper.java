package com.projectmanagement.mapper;

import com.projectmanagement.dto.*;
import com.projectmanagement.entity.Assignment;
import com.projectmanagement.entity.Category;
import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Notification;
import com.projectmanagement.entity.Project;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DtoMapper {

    @Autowired
    private ModelMapper modelMapper;

    // ----------------- Category -----------------
    public CategoryDTO toCategoryDTO(Category category) {
        if (category == null) return null;
        return modelMapper.map(category, CategoryDTO.class);
    }

    public Category toCategory(CategoryDTO dto) {
        if (dto == null) return null;
        Category c = new Category();
        c.setId(dto.getId());
        c.setName(dto.getName());
        return c;
    }

    // ----------------- Employee -----------------
    public EmployeeResponseDTO toEmployeeResponseDTO(Employee employee) {
        if (employee == null) return null;
        EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
        if (employee.getCategory() != null) dto.setCategoryId(employee.getCategory().getId());
        return dto;
    }

    public Employee toEmployeeFromCreateDTO(EmployeeCreateDTO dto) {
        if (dto == null) return null;
        Employee e = new Employee();
        e.setId(dto.getId());
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setEmail(dto.getEmail());
        e.setPassword(dto.getPassword());
        e.setRole(dto.getRole());
        if (dto.getCategoryId() != null) {
            Category c = new Category();
            c.setId(dto.getCategoryId());
            e.setCategory(c);
        }
        return e;
    }

    public Employee toEmployeeFromUpdateProfileDTO(com.projectmanagement.dto.UpdateProfileDTO dto) {
        if (dto == null) return null;
        Employee e = new Employee();
        e.setId(dto.getId());
        e.setFirstName(dto.getFirstName());
        e.setLastName(dto.getLastName());
        e.setEmail(dto.getEmail());
        e.setPassword(dto.getPassword());
        if (dto.getCategoryId() != null) {
            Category c = new Category();
            c.setId(dto.getCategoryId());
            e.setCategory(c);
        }
        return e;
    }

    // ----------------- Project -----------------
    public ProjectDTO toProjectDTO(Project project) {
        if (project == null) return null;
        return modelMapper.map(project, ProjectDTO.class);
    }

    public Project toProject(ProjectDTO dto) {
        if (dto == null) return null;
        Project p = new Project();
        p.setId(dto.getId());
        p.setTitle(dto.getTitle());
        p.setDescription(dto.getDescription());
        p.setStartDate(dto.getStartDate());
        p.setEndDate(dto.getEndDate());
        return p;
    }

    // ----------------- Assignment -----------------
    public AssignmentDTO toAssignmentDTO(Assignment assignment) {
        if (assignment == null) return null;
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setEmployeeId(assignment.getEmployee() != null ? assignment.getEmployee().getId() : null);
        dto.setEmployeeName(assignment.getEmployee() != null ? assignment.getEmployee().getFullName() : null);
        dto.setProjectId(assignment.getProject() != null ? assignment.getProject().getId() : null);
        dto.setProjectTitle(assignment.getProject() != null ? assignment.getProject().getTitle() : null);
        dto.setStartDate(assignment.getStartDate());
        dto.setEndDate(assignment.getEndDate());
        return dto;
    }

    public Assignment toAssignment(AssignmentDTO dto) {
        if (dto == null) return null;
        Assignment a = new Assignment();
        a.setId(dto.getId());
        if (dto.getEmployeeId() != null) {
            Employee e = new Employee();
            e.setId(dto.getEmployeeId());
            a.setEmployee(e);
        }
        if (dto.getProjectId() != null) {
            Project p = new Project();
            p.setId(dto.getProjectId());
            a.setProject(p);
        }
        a.setStartDate(dto.getStartDate());
        a.setEndDate(dto.getEndDate());
        return a;
    }

    // ----------------- Notification -----------------
    public NotificationDTO toNotificationDTO(Notification notification) {
        if (notification == null) return null;
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setRecipientId(notification.getRecipient() != null ? notification.getRecipient().getId() : null);
        dto.setRecipientName(notification.getRecipient() != null ? notification.getRecipient().getFullName() : null);
        dto.setMessage(notification.getMessage());
        dto.setSenderName(notification.getSenderName());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
