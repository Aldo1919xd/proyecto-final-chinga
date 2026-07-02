package com.app.ventas.service;

import com.app.ventas.entity.Funcionalidad;
import com.app.ventas.entity.Rol;
import com.app.ventas.entity.RolFuncionalidad;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.FuncionalidadRepository;
import com.app.ventas.repository.RolFuncionalidadRepository;
import com.app.ventas.repository.RolRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolService {

    private final RolRepository rolRepository;
    private final FuncionalidadRepository funcionalidadRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final AuditoriaService auditoriaService;

    public RolService(RolRepository rolRepository, FuncionalidadRepository funcionalidadRepository,
                      RolFuncionalidadRepository rolFuncionalidadRepository,
                      AuditoriaService auditoriaService) {
        this.rolRepository = rolRepository;
        this.funcionalidadRepository = funcionalidadRepository;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Rol> listarActivos() {
        return rolRepository.findByEstadoTrue();
    }

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Optional<Rol> buscarPorId(Integer id) {
        return rolRepository.findById(id);
    }

    @Transactional
    public Rol guardar(Rol rol, Usuario usuarioActual, HttpServletRequest request) {
        boolean esNuevo = rol.getIdRol() == null;
        Rol guardado = rolRepository.save(rol);
        auditoriaService.registrar(usuarioActual, "Seguridad", "Rol",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getIdRol(),
                esNuevo ? null : "{\"nombreRol\":\"" + rol.getNombreRol() + "\"}",
                "{\"nombreRol\":\"" + guardado.getNombreRol() + "\"}",
                request);
        return guardado;
    }

    @Transactional
    public void eliminarLogico(Integer id, Usuario usuarioActual, HttpServletRequest request) {
        Rol rol = rolRepository.findById(id).orElseThrow();
        if (rol.getIdRol() == 1) {
            throw new RuntimeException("No se puede eliminar al Superusuario");
        }
        rol.setEstado(false);
        rolRepository.save(rol);
        auditoriaService.registrar(usuarioActual, "Seguridad", "Rol",
                "DELETE", id, "{\"estado\":true}", "{\"estado\":false}", request);
    }

    public List<Funcionalidad> listarFuncionalidades() {
        return funcionalidadRepository.findByPadreIsNullOrderByNombreAsc();
    }

    public static class FuncionalidadTree {
        public Funcionalidad funcionalidad;
        public int depth;
        public boolean hasChildren;

        public FuncionalidadTree(Funcionalidad funcionalidad, int depth, boolean hasChildren) {
            this.funcionalidad = funcionalidad;
            this.depth = depth;
            this.hasChildren = hasChildren;
        }
    }

    public List<FuncionalidadTree> listarArbol() {
        List<Funcionalidad> raices = funcionalidadRepository.findByPadreIsNullOrderByNombreAsc();
        List<FuncionalidadTree> resultado = new ArrayList<>();
        for (Funcionalidad raiz : raices) {
            resultado.add(new FuncionalidadTree(raiz, 0, !raiz.getHijos().isEmpty()));
            agregarHijos(raiz, 1, resultado);
        }
        return resultado;
    }

    private void agregarHijos(Funcionalidad padre, int depth, List<FuncionalidadTree> resultado) {
        for (Funcionalidad hijo : padre.getHijos()) {
            resultado.add(new FuncionalidadTree(hijo, depth, !hijo.getHijos().isEmpty()));
            agregarHijos(hijo, depth + 1, resultado);
        }
    }

    public List<RolFuncionalidad> listarPermisosPorRol(Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol);
    }

    public Map<Integer, RolFuncionalidad> listarPermisosMap(Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol)
                .stream()
                .collect(Collectors.toMap(rf -> rf.getFuncionalidad().getIdFuncionalidad(), rf -> rf));
    }

    @Transactional
    public void guardarPermisos(Integer idRol,
                                List<Integer> ver, List<Integer> crear,
                                List<Integer> editar, List<Integer> eliminar,
                                List<Integer> imprimir,
                                Usuario usuarioActual, HttpServletRequest request) {
        Rol rol = rolRepository.findById(idRol).orElseThrow();
        List<Funcionalidad> todas = funcionalidadRepository.findAll();

        rolFuncionalidadRepository.deleteByRolIdRol(idRol);

        for (Funcionalidad func : todas) {
            Integer fid = func.getIdFuncionalidad();
            RolFuncionalidad rf = new RolFuncionalidad();
            rf.setRol(rol);
            rf.setFuncionalidad(func);
            rf.setVer(ver != null && ver.contains(fid));
            rf.setCrear(crear != null && crear.contains(fid));
            rf.setEditar(editar != null && editar.contains(fid));
            rf.setEliminar(eliminar != null && eliminar.contains(fid));
            rf.setImprimir(imprimir != null && imprimir.contains(fid));
            rolFuncionalidadRepository.save(rf);
        }

        auditoriaService.registrar(usuarioActual, "Seguridad", "RolFuncionalidad",
                "UPDATE", idRol, null,
                "{\"permisosActualizados\":true}", request);
    }
}
