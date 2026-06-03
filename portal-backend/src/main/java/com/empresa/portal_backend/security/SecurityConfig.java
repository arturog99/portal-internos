package com.empresa.portal_backend.security;

import com.empresa.portal_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración principal de seguridad de la aplicación.
 * 
 * Esta clase configura:
 * - Autenticación JWT mediante filtros personalizados
 * - Autenticación con certificado digital X.509 (opcional)
 * - Control de acceso basado en roles (RBAC)
 * - Configuración CORS para permitir peticiones desde el frontend
 * - Gestión de sesiones sin estado (stateless)
 * 
 * Roles disponibles:
 * - ADMIN: Acceso completo a todos los endpoints
 * - TECNICO: Acceso de lectura/escritura a proyectos
 * - VISITANTE: Solo lectura de proyectos
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserRepository userRepository;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.certificate.enabled:false}")
    private boolean certificateEnabled;

    /**
     * Regex para extraer el identificador del subject del certificado X.509.
     * Por defecto extrae el Common Name (CN).
     * Para certificados FNMT con el DNI en SERIALNUMBER usar: "SERIALNUMBER=([^,]+)"
     */
    @Value("${app.certificate.subject-regex:CN=(.*?)(?:,|$)}")
    private String certificateSubjectRegex;

    /**
     * Constructor de la configuración de seguridad.
     *
     * @param jwtAuthenticationFilter Filtro para validar tokens JWT en cada petición
     * @param userRepository Repositorio para buscar usuarios en autenticación por certificado
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, UserRepository userRepository) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userRepository = userRepository;
    }

    /**
     * Configura la cadena de filtros de seguridad de la aplicación.
     * 
     * Define las reglas de acceso a los endpoints según el rol del usuario:
     * - Endpoints públicos: /api/auth/**, /error
     * - GET /api/projects/**: ADMIN, TECNICO, VISITANTE
     * - POST /api/projects: ADMIN
     * - DELETE /api/projects/**: ADMIN
     * - PUT/PATCH /api/projects/**: ADMIN, TECNICO
     * - /api/users/**: ADMIN
     * 
     * @param http Configuración HTTP de Spring Security
     * @return Cadena de filtros de seguridad configurada
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos de autenticación
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Lectura de proyectos: cualquier rol autenticado
                        .requestMatchers(HttpMethod.GET, "/api/projects/**")
                            .hasAnyRole("ADMIN", "TECNICO", "VISITANTE")
                        // Crear / borrar proyectos: solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                        // Editar proyectos / estado: ADMIN y TECNICO
                        .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasAnyRole("ADMIN", "TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/projects/**").hasAnyRole("ADMIN", "TECNICO")
                        // Gestión de usuarios: solo ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Autenticación con certificado digital (X.509 / FNMT), activable por configuración
        if (certificateEnabled) {
            http.x509(x509 -> x509
                    .subjectPrincipalRegex(certificateSubjectRegex)
                    .userDetailsService(new X509UserDetailsService(userRepository)));
        }

        return http.build();
    }

    /**
     * Configura las políticas CORS para permitir peticiones desde el frontend.
     * 
     * Permite:
     * - Orígenes configurados en application.properties
     * - Métodos HTTP: GET, POST, PUT, PATCH, DELETE, OPTIONS
     * - Todos los headers
     * - Credenciales (cookies, authorization headers)
     * 
     * @return Configuración CORS para la aplicación
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Proporciona el codificador de contraseñas usando BCrypt.
     * 
     * @return Codificador de contraseñas BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proporciona el gestor de autenticación de Spring Security.
     * 
     * @param config Configuración de autenticación
     * @return Gestor de autenticación configurado
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
