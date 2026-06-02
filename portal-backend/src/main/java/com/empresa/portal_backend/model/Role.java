package com.empresa.portal_backend.model;

// Roles de usuario del portal y su nivel de permisos:
// ADMIN     -> control total (CRUD proyectos + gestion de usuarios)
// TECNICO   -> control acotado (editar proyectos y su estado, sin borrar ni gestionar usuarios)
// VISITANTE -> solo visualizacion
public enum Role {
    ADMIN,
    TECNICO,
    VISITANTE
}
