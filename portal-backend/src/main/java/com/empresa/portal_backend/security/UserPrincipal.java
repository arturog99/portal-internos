package com.empresa.portal_backend.security;

import com.empresa.portal_backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre nuestra entidad User y el UserDetails de Spring Security.
 * 
 * Esta clase envuelve la entidad User y la adapta a la interfaz UserDetails
 * que Spring Security utiliza para la autenticación y autorización.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    /**
     * Constructor que envuelve una entidad User.
     *
     * @param user Entidad de usuario a adaptar
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Obtiene la entidad User original.
     *
     * @return Entidad User envuelta
     */
    public User getUser() {
        return user;
    }

    /**
     * Obtiene los roles/autoridades del usuario.
     * 
     * Spring Security espera el prefijo ROLE_ para usar hasRole(...).
     *
     * @return Lista de autoridades con el rol del usuario
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
