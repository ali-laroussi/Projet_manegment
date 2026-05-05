package com.projectmanagement.config;

import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Category;
import com.projectmanagement.entity.UserRole;
import com.projectmanagement.repository.EmployeeRepository;
import com.projectmanagement.repository.CategoryRepository;
import com.projectmanagement.repository.ProjectRepository;
import com.projectmanagement.repository.AssignmentRepository;
import com.projectmanagement.entity.Project;
import com.projectmanagement.entity.Assignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing database with test data...");

        try {
            // Check if data already exists
            long userCount = employeeRepository.count();
            if (userCount > 0) {
                System.out.println("Database already initialized with " + userCount + " users. Skipping initialization.");
                return;
            }

            // Initialize Categories
            System.out.println("Creating categories...");
            Category devCategory = new Category();
            devCategory.setName("Developer");
            devCategory = categoryRepository.save(devCategory);

            Category managerCategory = new Category();
            managerCategory.setName("Manager");
            managerCategory = categoryRepository.save(managerCategory);

            Category designerCategory = new Category();
            designerCategory.setName("Designer");
            designerCategory = categoryRepository.save(designerCategory);

            Category devopsCategory = new Category();
            devopsCategory.setName("DevOps");
            devopsCategory = categoryRepository.save(devopsCategory);

            Category qaCategory = new Category();
            qaCategory.setName("QA");
            qaCategory = categoryRepository.save(qaCategory);

            System.out.println("Categories created successfully");

            // Initialize Employees with properly hashed passwords
            System.out.println("Creating employees...");
            String hashedPassword = passwordEncoder.encode("password123");

            Employee admin = new Employee();
            admin.setFirstName("Jean");
            admin.setLastName("Admin");
            admin.setEmail("admin@company.com");
            admin.setPassword(hashedPassword);
            admin.setRole(UserRole.ADMIN);
            admin.setCategory(managerCategory);
            admin = employeeRepository.save(admin);
            System.out.println("✓ Admin user created: " + admin.getEmail());

        Employee alice = new Employee();
        alice.setFirstName("Alice");
        alice.setLastName("Dupont");
        alice.setEmail("alice.dupont@company.com");
        alice.setPassword(hashedPassword);
        alice.setRole(UserRole.EMPLOYEE);
        alice.setCategory(devCategory);
        alice = employeeRepository.save(alice);

        Employee bob = new Employee();
        bob.setFirstName("Bob");
        bob.setLastName("Martin");
        bob.setEmail("bob.martin@company.com");
        bob.setPassword(hashedPassword);
        bob.setRole(UserRole.EMPLOYEE);
        bob.setCategory(devCategory);
        bob = employeeRepository.save(bob);

        Employee claire = new Employee();
        claire.setFirstName("Claire");
        claire.setLastName("Bernard");
        claire.setEmail("claire.bernard@company.com");
        claire.setPassword(hashedPassword);
        claire.setRole(UserRole.EMPLOYEE);
        claire.setCategory(designerCategory);
        claire = employeeRepository.save(claire);

        Employee david = new Employee();
        david.setFirstName("David");
        david.setLastName("Lefevre");
        david.setEmail("david.lefevre@company.com");
        david.setPassword(hashedPassword);
        david.setRole(UserRole.EMPLOYEE);
        david.setCategory(devCategory);
        david = employeeRepository.save(david);

        Employee emma = new Employee();
        emma.setFirstName("Emma");
        emma.setLastName("Moreau");
        emma.setEmail("emma.moreau@company.com");
        emma.setPassword(hashedPassword);
        emma.setRole(UserRole.EMPLOYEE);
        emma.setCategory(devopsCategory);
        emma = employeeRepository.save(emma);

        Employee frank = new Employee();
        frank.setFirstName("Frank");
        frank.setLastName("Garcia");
        frank.setEmail("frank.garcia@company.com");
        frank.setPassword(hashedPassword);
        frank.setRole(UserRole.EMPLOYEE);
        frank.setCategory(qaCategory);
        frank = employeeRepository.save(frank);

        Employee grace = new Employee();
        grace.setFirstName("Grace");
        grace.setLastName("Laurent");
        grace.setEmail("grace.laurent@company.com");
        grace.setPassword(hashedPassword);
        grace.setRole(UserRole.EMPLOYEE);
        grace.setCategory(managerCategory);
        grace = employeeRepository.save(grace);

        // Initialize Projects
        Project project1 = new Project();
        project1.setTitle("E-Commerce Platform");
        project1.setDescription("Development of the online sales platform");
        project1.setStartDate(LocalDate.of(2024, 1, 15));
        project1.setEndDate(LocalDate.of(2024, 12, 31));
        project1 = projectRepository.save(project1);

        Project project2 = new Project();
        project2.setTitle("Mobile App iOS");
        project2.setDescription("Mobile application for iOS");
        project2.setStartDate(LocalDate.of(2024, 2, 1));
        project2.setEndDate(LocalDate.of(2024, 10, 30));
        project2 = projectRepository.save(project2);

        Project project3 = new Project();
        project3.setTitle("API REST Refactoring");
        project3.setDescription("Refactoring of the existing API");
        project3.setStartDate(LocalDate.of(2023, 6, 1));
        project3.setEndDate(LocalDate.of(2023, 12, 15));
        project3 = projectRepository.save(project3);

        Project project4 = new Project();
        project4.setTitle("Dashboard Analytics");
        project4.setDescription("Analysis dashboard");
        project4.setStartDate(LocalDate.of(2024, 5, 1));
        project4.setEndDate(LocalDate.of(2024, 11, 30));
        project4 = projectRepository.save(project4);

        Project project5 = new Project();
        project5.setTitle("Database Migration");
        project5.setDescription("Migration to PostgreSQL");
        project5.setStartDate(LocalDate.of(2024, 3, 10));
        project5.setEndDate(LocalDate.of(2024, 9, 30));
        project5 = projectRepository.save(project5);

        Project project6 = new Project();
        project6.setTitle("Notification System");
        project6.setDescription("Real-time notification system");
        project6.setStartDate(LocalDate.of(2024, 4, 15));
        project6.setEndDate(LocalDate.of(2024, 10, 31));
        project6 = projectRepository.save(project6);

        Project project7 = new Project();
        project7.setTitle("Internal HR Application");
        project7.setDescription("HR management software");
        project7.setStartDate(LocalDate.of(2023, 1, 10));
        project7.setEndDate(LocalDate.of(2023, 8, 30));
        project7 = projectRepository.save(project7);

        Project project8 = new Project();
        project8.setTitle("Cloud Infrastructure");
        project8.setDescription("Deployment on AWS");
        project8.setStartDate(LocalDate.of(2024, 1, 20));
        project8.setEndDate(LocalDate.of(2024, 12, 31));
        project8 = projectRepository.save(project8);

        // Initialize Assignments
        createAssignment(alice, project1, LocalDate.of(2024, 1, 15), LocalDate.of(2024, 12, 31));
        createAssignment(bob, project1, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 12, 31));
        createAssignment(claire, project2, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 10, 30));
        createAssignment(david, project1, LocalDate.of(2024, 3, 1), LocalDate.of(2024, 12, 31));
        createAssignment(emma, project5, LocalDate.of(2024, 3, 10), LocalDate.of(2024, 9, 30));
        createAssignment(frank, project8, LocalDate.of(2024, 1, 20), LocalDate.of(2024, 12, 31));
        createAssignment(alice, project4, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 11, 30));
        createAssignment(bob, project6, LocalDate.of(2024, 4, 15), LocalDate.of(2024, 10, 31));
        createAssignment(grace, project1, LocalDate.of(2024, 4, 1), LocalDate.of(2024, 12, 31));
        createAssignment(grace, project5, LocalDate.of(2024, 3, 15), LocalDate.of(2024, 9, 30));

        System.out.println("Database initialization completed!");
        System.out.println("Total employees: " + employeeRepository.count());
        System.out.println("Total projects: " + projectRepository.count());
        System.out.println("Total assignments: " + assignmentRepository.count());
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createAssignment(Employee employee, Project project, LocalDate startDate, LocalDate endDate) {
        Assignment assignment = new Assignment();
        assignment.setEmployee(employee);
        assignment.setProject(project);
        assignment.setStartDate(startDate);
        assignment.setEndDate(endDate);
        assignmentRepository.save(assignment);
    }
}
