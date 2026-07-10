package com.app.ventas.service;

import com.app.ventas.entity.RolFuncionalidad;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.RolFuncionalidadRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class PermisoService {

    private final UsuarioService usuarioService;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;

    public PermisoService(UsuarioService usuarioService, RolFuncionalidadRepository rolFuncionalidadRepository) {
        this.usuarioService = usuarioService;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
    }

    private boolean check(Authentication auth, String funcionalidadNombre, Function<RolFuncionalidad, Boolean> permisoFn) {
        if (auth == null || !auth.isAuthenticated()) return false;
        Usuario usuario = usuarioService.buscarPorUsuario(auth.getName()).orElse(null);
        if (usuario == null) return false;
        List<RolFuncionalidad> rfList = rolFuncionalidadRepository.findByRolIdRol(usuario.getRol().getIdRol());
        return rfList.stream()
                .filter(rf -> rf.getFuncionalidad().getNombre().equals(funcionalidadNombre))
                .findFirst()
                .map(permisoFn)
                .orElse(false);
    }

    public boolean tieneVer(Authentication auth, String funcionalidad) {
        return check(auth, funcionalidad, RolFuncionalidad::getVer);
    }

    public boolean tieneCrear(Authentication auth, String funcionalidad) {
        return check(auth, funcionalidad, RolFuncionalidad::getCrear);
    }

    public boolean tieneEditar(Authentication auth, String funcionalidad) {
        return check(auth, funcionalidad, RolFuncionalidad::getEditar);
    }

    public boolean tieneEliminar(Authentication auth, String funcionalidad) {
        return check(auth, funcionalidad, RolFuncionalidad::getEliminar);
    }
}
