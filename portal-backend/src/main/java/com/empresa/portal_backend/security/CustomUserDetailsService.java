package com.empresa.portal_backend.security;

import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio de detalles de usuario para autenticación JWT.
 * 
 * Implementa UserDetailsService de Spring Security para cargar los detalles
 * del usuario desde la base de datos durante el proceso de autenticación.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor del servicio de detalles de usuario.
     *
     * @param userRepository Repositorio de usuarios
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles del usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return UserDetails de Spring Security con la información del usuario
     * @throws UsernameNotFoundException Si no existe el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserPrincipal(user);
    }
}
