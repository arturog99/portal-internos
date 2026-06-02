package com.empresa.portal_backend.config;

import com.empresa.portal_backend.model.Project;
import com.empresa.portal_backend.model.Role;
import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.ProjectRepository;
import com.empresa.portal_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Carga datos iniciales al arrancar: 3 usuarios (uno por rol) y los proyectos de ejemplo.
// Solo se ejecuta si las tablas estan vacias, por lo que es seguro reiniciar.
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      ProjectRepository projectRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedProjects();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        log.info("Seed: creando usuarios por defecto (admin / tecnico / visitante)");

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@empresa.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .totpEnabled(false)
                .build());

        userRepository.save(User.builder()
                .username("tecnico")
                .email("tecnico@empresa.com")
                .password(passwordEncoder.encode("tecnico123"))
                .role(Role.TECNICO)
                .enabled(true)
                .totpEnabled(false)
                .build());

        userRepository.save(User.builder()
                .username("visitante")
                .email("visitante@empresa.com")
                .password(passwordEncoder.encode("visitante123"))
                .role(Role.VISITANTE)
                .enabled(true)
                .totpEnabled(false)
                .build());
    }

    private void seedProjects() {
        if (projectRepository.count() > 0) {
            return;
        }
        log.info("Seed: cargando proyectos de ejemplo");

        List<SeedProject> seeds = List.of(
                new SeedProject("Gestor de Incidencias",
                        "Plataforma interna para el reporte y seguimiento de tickets de soporte tecnico.",
                        List.of("Angular", "Node.js", "MongoDB"), "Producción"),
                new SeedProject("App de Fichajes",
                        "Aplicacion para el control horario y registro de jornadas de los empleados.",
                        List.of("React Native", "Firebase"), "Mantenimiento"),
                new SeedProject("Intranet Corporativa",
                        "Portal centralizado para noticias, documentos y comunicacion de la empresa.",
                        List.of("Vue", "PHP", "MySQL"), "Producción"),
                new SeedProject("Renove de Equipos",
                        "Herramienta para gestionar la sustitucion del hardware obsoleto de la plantilla.",
                        List.of("HTML/CSS", "JavaScript", "SQL"), "En Desarrollo"),
                new SeedProject("Informes Mensuales",
                        "Automatizacion de reportes financieros y de rendimiento para direccion.",
                        List.of("Python", "Pandas", "AWS"), "Producción"),
                new SeedProject("Chat Interno Seguro",
                        "Canal de comunicacion seguro y en tiempo real para proyectos confidenciales.",
                        List.of("Angular", "WebSockets", "Java"), "En Desarrollo"),
                new SeedProject("Inventario de Almacen",
                        "Sistema de tracking de stock y logistica en tiempo real.",
                        List.of("React", "Express", "PostgreSQL"), "Mantenimiento"),
                new SeedProject("Dashboard de Ventas",
                        "Panel interactivo con graficas de rendimiento comercial.",
                        List.of("Angular", "Tailwind", "TypeScript"), "Producción")
        );

        for (SeedProject s : seeds) {
            projectRepository.save(Project.builder()
                    .name(s.name())
                    .description(s.description())
                    .tags(new ArrayList<>(s.tags()))
                    .status(s.status())
                    .build());
        }
        log.info("Seed: {} proyectos cargados", seeds.size());
    }

    private record SeedProject(String name, String description, List<String> tags, String status) {
    }
}
