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

        if (tipoDocumentoRepository.count() == 0) {
            for (String d : new String[]{"DNI", "RUC", "CE", "Pasaporte"}) {
                TipoDocumento td = new TipoDocumento();
                td.setDescripcion(d);
                td.setEstado(true);
                tipoDocumentoRepository.save(td);
            }
        }

        if (tipoOperacionRepository.count() == 0) {
            for (String op : new String[]{"Ingreso", "Venta", "Extorno", "Ajuste"}) {
                TipoOperacion to = new TipoOperacion();
                to.setDescripcion(op);
                tipoOperacionRepository.save(to);
            }
        }

        if (funcionalidadRepository.count() == 0) {
            for (String nombre : new String[]{"Dashboard", "Usuarios", "Roles",
                    "Clientes", "Categorias", "Productos", "Ingresos",
                    "Ventas", "Kardex", "Auditoria"}) {
                Funcionalidad f = new Funcionalidad();
                f.setNombre(nombre);
                funcionalidadRepository.save(f);
            }
        }

        if (rolFuncionalidadRepository.count() == 0) {
            var roles = rolRepository.findAll();
            var funcionalidades = funcionalidadRepository.findAll();

            for (Rol r : roles) {
                for (Funcionalidad f : funcionalidadRepository.findAll()) {
                    RolFuncionalidad rf = new RolFuncionalidad();
                    rf.setRol(r);
                    rf.setFuncionalidad(f);
                    rf.setVer(true);

                    if (r.getIdRol() == 1) {
                        rf.setCrear(true);
                        rf.setEditar(true);
                        rf.setEliminar(true);
                        if (f.getNombre().equals("Dashboard") || f.getNombre().equals("Kardex")
                                || f.getNombre().equals("Auditoria")) {
                            rf.setEliminar(false);
                            rf.setEditar(false);
                            rf.setCrear(false);
                        }
                        if (f.getNombre().equals("Clientes") || f.getNombre().equals("Productos")
                                || f.getNombre().equals("Ventas") || f.getNombre().equals("Kardex")) {
                            rf.setImprimir(true);
                        }
                    } else if (r.getIdRol() == 2) {
                        if (!f.getNombre().equals("Clientes") && !f.getNombre().equals("Productos")
                                && !f.getNombre().equals("Ventas") && !f.getNombre().equals("Dashboard")
                                && !f.getNombre().equals("Kardex")) {
                            rf.setVer(false);
                        }
                        if (f.getNombre().equals("Clientes")) {
                            rf.setCrear(true);
                            rf.setEditar(true);
                        }
                        if (f.getNombre().equals("Ventas")) {
                            rf.setCrear(true);
                            rf.setImprimir(true);
                        }
                        if (f.getNombre().equals("Productos") || f.getNombre().equals("Kardex")) {
                            rf.setImprimir(true);
                        }
                    } else if (r.getIdRol() == 3) {
                        if (!f.getNombre().equals("Productos") && !f.getNombre().equals("Categorias")
                                && !f.getNombre().equals("Ingresos") && !f.getNombre().equals("Kardex")
                                && !f.getNombre().equals("Dashboard")) {
                            rf.setVer(false);
                        }
                        if (f.getNombre().equals("Productos") || f.getNombre().equals("Categorias")
                                || f.getNombre().equals("Ingresos")) {
                            rf.setCrear(true);
                            rf.setEditar(true);
                        }
                        if (f.getNombre().equals("Productos") || f.getNombre().equals("Kardex")) {
                            rf.setImprimir(true);
                        }
                    } else if (r.getIdRol() == 4) {
                        if (!f.getNombre().equals("Dashboard") && !f.getNombre().equals("Ventas")
                                && !f.getNombre().equals("Kardex") && !f.getNombre().equals("Auditoria")) {
                            rf.setVer(false);
                        }
                        if (f.getNombre().equals("Ventas") || f.getNombre().equals("Kardex")) {
                            rf.setImprimir(true);
                        }
                    }
                    rolFuncionalidadRepository.save(rf);
                }
            }
        }

        if (usuarioRepository.count() == 0) {
            Rol superRol = rolRepository.findByEstadoTrue().get(0);
            Usuario admin = new Usuario();
            admin.setUsuario("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRol(superRol);
            admin.setEstado(true);
            usuarioRepository.save(admin);
        }
    }
}
