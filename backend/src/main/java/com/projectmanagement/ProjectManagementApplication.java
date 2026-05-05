package com.projectmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale pour démarrer l'application Spring Boot
 * Point d'entrée de l'application de gestion de projets
 */
@SpringBootApplication
public class ProjectManagementApplication {

    /**
     * Méthode main pour démarrer l'application
     * 
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(ProjectManagementApplication.class, args);
    }
}
