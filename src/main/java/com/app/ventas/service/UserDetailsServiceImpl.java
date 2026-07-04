package com.app.ventas.service;

import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.ventas.entity.RolFuncionalidad;
import com.app.ventas.repository.RolFuncionalidadRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, RolFuncionalidadRepository rolFuncionalidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.getEstado()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol().toUpperCase()));
        
        List<RolFuncionalidad> permisos = rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol());
        for (RolFuncionalidad p : permisos) {
            String funcName = p.getFuncionalidad().getNombre().toUpperCase().replace(" ", "_");
            if (p.getVer()) authorities.add(new SimpleGrantedAuthority("VER_" + funcName));
            if (p.getCrear()) authorities.add(new SimpleGrantedAuthority("CREAR_" + funcName));
            if (p.getEditar()) authorities.add(new SimpleGrantedAuthority("EDITAR_" + funcName));
            if (p.getEliminar()) authorities.add(new SimpleGrantedAuthority("ELIMINAR_" + funcName));
        }

        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();
    }
}
