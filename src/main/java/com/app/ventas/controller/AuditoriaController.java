package com.app.ventas.controller;

import com.app.ventas.entity.Auditoria;
import com.app.ventas.repository.AuditoriaRepository;
import com.app.ventas.service.PermisoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;
    private final PermisoService permisoService;

    public AuditoriaController(AuditoriaRepository auditoriaRepository, PermisoService permisoService) {
        this.auditoriaRepository = auditoriaRepository;
        this.permisoService = permisoService;
    }

    @GetMapping
    public String lista(
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String operacion,
            Authentication auth, Model model) {
        if (!permisoService.tieneVer(auth, "Auditoria")) return "redirect:/inicio?error=sinPermiso";
        
        List<Auditoria> registros;
        if (modulo != null && !modulo.isEmpty() && operacion != null && !operacion.isEmpty()) {
            registros = auditoriaRepository.findByModuloAndOperacionOrderByFechaHoraDesc(modulo, operacion);
        } else if (modulo != null && !modulo.isEmpty()) {
            registros = auditoriaRepository.findByModuloOrderByFechaHoraDesc(modulo);
        } else if (operacion != null && !operacion.isEmpty()) {
            registros = auditoriaRepository.findByOperacionOrderByFechaHoraDesc(operacion);
        } else {
            registros = auditoriaRepository.findAllByOrderByFechaHoraDesc();
        }
        
        model.addAttribute("registros", registros);
        return "auditoria/lista";
    }
}
