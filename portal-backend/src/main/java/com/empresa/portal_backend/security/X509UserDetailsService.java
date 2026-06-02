package com.empresa.portal_backend.security;

import com.empresa.portal_backend.model.User;
import com.empresa.portal_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// UserDetailsService usado por la autenticacion con certificado digital (X.509 / FNMT).
// El identificador extraido del subject del certificado se compara con el campo
// certificateId de los usuarios registrados.
public class X509UserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public X509UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String certificatePrincipal) throws UsernameNotFoundException {
        User user = userRepository.findByCertificateId(certificatePrincipal)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Ningun usuario asociado al certificado: " + certificatePrincipal));
        return new UserPrincipal(user);
    }
}
