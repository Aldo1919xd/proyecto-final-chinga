package com.app.ventas.config;

import com.app.ventas.entity.*;
import com.app.ventas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final TipoOperacionRepository tipoOperacionRepository;
    private final FuncionalidadRepository funcionalidadRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                           TipoDocumentoRepository tipoDocumentoRepository,
                           TipoOperacionRepository tipoOperacionRepository,
                           FuncionalidadRepository funcionalidadRepository,
                           RolFuncionalidadRepository rolFuncionalidadRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.tipoOperacionRepository = tipoOperacionRepository;
        this.funcionalidadRepository = funcionalidadRepository;
        this.rolFuncionalidadRepository = rolFuncionalidadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            Rol superRol = new Rol();
            superRol.setNombreRol("Superusuario");
            superRol.setEstado(true);
            rolRepository.save(superRol);

            Rol vendedor = new Rol();
            vendedor.setNombreRol("Vendedor");
            vendedor.setEstado(true);
            rolRepository.save(vendedor);

            Rol almacen = new Rol();
            almacen.setNombreRol("Almacen");
            almacen.setEstado(true);
            rolRepository.save(almacen);

            Rol contabilidad = new Rol();
            contabilidad.setNombreRol("Contabilidad");
            contabilidad.setEstado(true);
            rolRepository.save(contabilidad);
        }

        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsuario("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRol(rolRepository.findByEstadoTrue().get(0));
            admin.setEstado(true);
            usuarioRepository.save(admin);
        }

        if (tipoDocumentoRepository.count() == 0) {
            String[] docs = {"DNI", "RUC", "CE", "Pasaporte"};
            for (String d : docs) {
                TipoDocumento td = new TipoDocumento();
                td.setDescripcion(d);
                td.setEstado(true);
                tipoDocumentoRepository.save(td);
            }
        }

        if (tipoOperacionRepository.count() == 0) {
            String[] ops = {"Ingreso", "Venta", "Extorno", "Ajuste"};
            for (int i = 0; i < ops.length; i++) {
                TipoOperacion to = new TipoOperacion();
                to.setDescripcion(ops[i]);
                tipoOperacionRepository.save(to);
            }
        }

        if (funcionalidadRepository.count() == 0) {
            Funcionalidad kardex = new Funcionalidad();
            kardex.setNombre("Kardex");
            funcionalidadRepository.save(kardex);

            Funcionalidad auditoria = new Funcionalidad();
            auditoria.setNombre("Auditoría");
            funcionalidadRepository.save(auditoria);

            Rol superRol = rolRepository.findByEstadoTrue().get(0);

            for (Funcionalidad f : funcionalidadRepository.findAll()) {
                RolFuncionalidad rf = new RolFuncionalidad();
                rf.setRol(superRol);
                rf.setFuncionalidad(f);
                rf.setVer(true);
                rf.setCrear(true);
                rf.setEditar(true);
                rf.setEliminar(true);
                rf.setImprimir(true);
                rolFuncionalidadRepository.save(rf);
            }
        }
    }
}
