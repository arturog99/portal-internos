package com.empresa.portal_backend.security;

import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * UserDetailsService usado por la autenticación con certificado digital (X.509 / FNMT).
 * 
 * El identificador extraído del subject del certificado se compara con el campo
 * certificateId de los usuarios registrados para autenticarlos.
 */
public class X509UserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor del servicio de autenticación por certificado.
     *
     * @param userRepository Repositorio de usuarios
     */
    public X509UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles del usuario por el identificador del certificado.
     *
     * @param certificatePrincipal Identificador extraído del subject del certificado
     * @return UserDetails de Spring Security con la información del usuario
     * @throws UsernameNotFoundException Si no existe un usuario asociado al certificado
     */
    @Override
    public UserDetails loadUserByUsername(String certificatePrincipal) throws UsernameNotFoundException {
        User user = userRepository.findByCertificateId(certificatePrincipal)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Ningun usuario asociado al certificado: " + certificatePrincipal));
        return new UserPrincipal(user);
    }
}
