package com.empresa.portal_backend.model;

/**
 * Enumeración de roles de usuario del portal y su nivel de permisos.
 * 
 * Roles disponibles:
 * - ADMIN: Control total (CRUD proyectos + gestión de usuarios)
 * - TECNICO: Control acotado (editar proyectos y su estado, sin borrar ni gestionar usuarios)
 * - VISITANTE: Solo visualización
 */
public enum Role {
    /**
     * Rol con acceso completo a todas las funcionalidades.
     */
    ADMIN,
    
    /**
     * Rol con acceso limitado a edición de proyectos.
     */
    TECNICO,
    
    /**
     * Rol con acceso solo de lectura.
     */
    VISITANTE
}
