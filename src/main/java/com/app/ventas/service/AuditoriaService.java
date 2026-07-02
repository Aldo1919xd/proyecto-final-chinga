package com.app.ventas.service;

import com.app.ventas.entity.Auditoria;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.AuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(Usuario usuario, String modulo, String tabla, String operacion,
                          Integer codigoRegistro, String valorAnterior, String valorNuevo,
                          HttpServletRequest request) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuario);
        a.setModulo(modulo);
        a.setTablaAfectada(tabla);
        a.setOperacion(operacion);
        a.setCodigoRegistro(codigoRegistro);
        a.setValorAnterior(valorAnterior);
        a.setValorNuevo(valorNuevo);
        a.setFechaHora(LocalDateTime.now());
        if (request != null) {
            a.setIpOrigen(request.getRemoteAddr());
            a.setEquipo(request.getRemoteHost());
            a.setNavegador(request.getHeader("User-Agent"));
        }
        auditoriaRepository.save(a);
    }
}
