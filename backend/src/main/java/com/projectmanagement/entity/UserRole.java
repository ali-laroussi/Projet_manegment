package com.projectmanagement.entity;

/**
 * Énumération des rôles disponibles dans le système
 */
public enum UserRole {
    /**
     * Rôle administrateur - accès complet au système
     */
    ADMIN("ADMIN"),

    /**
     * Rôle employé - accès limité
     */
    EMPLOYEE("EMPLOYEE");

    /**
     * Valeur du rôle
     */
    private final String value;

    /**
     * Constructeur
     */
    UserRole(String value) {
        this.value = value;
    }

    /**
     * Retourne la valeur du rôle
     */
    public String getValue() {
        return value;
    }

    /**
     * Convertit une chaîne en UserRole
     */
    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rôle invalide : " + value);
    }
}
