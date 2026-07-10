package com.app.ventas.config;

import com.app.ventas.entity.RolFuncionalidad;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.RolFuncionalidadRepository;
import com.app.ventas.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UsuarioService usuarioService;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;

    public GlobalControllerAdvice(UsuarioService usuarioService, RolFuncionalidadRepository rolFuncionalidadRepository) {
        this.usuarioService = usuarioService;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
    }

    @ModelAttribute("permisosMap")
    public Map<String, RolFuncionalidad> inyectarPermisosMap() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.buscarPorUsuario(auth.getName()).orElse(null);
            if (usuario != null) {
                return rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol())
                        .stream()
                        .collect(Collectors.toMap(
                                rf -> rf.getFuncionalidad().getNombre(),
                                rf -> rf
                        ));
            }
        }
        return Collections.emptyMap();
    }
}
