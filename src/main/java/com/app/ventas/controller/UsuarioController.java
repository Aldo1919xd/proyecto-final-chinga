package com.app.ventas.controller;

import com.app.ventas.entity.Usuario;
import com.app.ventas.service.RolService;
import com.app.ventas.service.UsuarioService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("user", new Usuario());
        model.addAttribute("roles", rolService.listarActivos());
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("user") Usuario usuario, BindingResult result,
                          Model model, Authentication auth, HttpServletRequest request) {
        boolean esNuevo = usuario.getIdUsuario() == null;

        if (esNuevo && (usuario.getPassword() == null || usuario.getPassword().isBlank())) {
            result.rejectValue("password", "error.usuario", "La contrasena es obligatoria");
        }
        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()
                && usuario.getPassword().length() < 6) {
            result.rejectValue("password", "error.usuario", "La contrasena debe tener al menos 6 caracteres");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.listarActivos());
            return "usuarios/formulario";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.guardar(usuario, actual, request);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("user", usuarioService.buscarPorId(id).orElseThrow());
        model.addAttribute("roles", rolService.listarActivos());
        return "usuarios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, Authentication auth, HttpServletRequest request) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.eliminarLogico(id, actual, request);
        return "redirect:/usuarios";
    }

    @GetMapping("/cambiar-password")
    public String cambiarPasswordForm() {
        return "usuarios/cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordNueva,
                                  @RequestParam(required = false) String passwordConfirmacion,
                                  Authentication auth, HttpServletRequest request, Model model) {
        if (passwordNueva == null || passwordNueva.length() < 6) {
            model.addAttribute("error", "La contrasena debe tener al menos 6 caracteres");
            return "usuarios/cambiar-password";
        }
        if (!passwordNueva.equals(passwordConfirmacion)) {
            model.addAttribute("error", "Las contrasenas no coinciden");
            return "usuarios/cambiar-password";
        }
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        usuarioService.cambiarPassword(actual.getIdUsuario(), passwordNueva, actual, request);
        return "redirect:/inicio";
    }

    @GetMapping("/2fa-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificar2fa(Authentication auth) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElse(null);
        boolean has2fa = actual != null && actual.getSecretKey2fa() != null && !actual.getSecretKey2fa().isEmpty();
        return ResponseEntity.ok(Map.of("has2fa", has2fa));
    }

    @GetMapping("/2fa/configurar")
    public String configurar2fa(Model model, Authentication auth) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        if (actual.getSecretKey2fa() != null && !actual.getSecretKey2fa().isEmpty()) {
            return "redirect:/usuarios/2fa/estado";
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        String uri = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                "AppVentas", auth.getName(), key);

        model.addAttribute("secret", secret);
        model.addAttribute("uri", uri);
        model.addAttribute("usuario", auth.getName());

        return "usuarios/2fa-configurar";
    }

    @PostMapping("/2fa/verificar")
    public String verificar2fa(@RequestParam String secret,
                               @RequestParam int codigo,
                               Authentication auth, Model model) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean valido = gAuth.authorize(secret, codigo);

        if (!valido) {
            GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret).build();
            String uri = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                    "AppVentas", auth.getName(), key);
            model.addAttribute("secret", secret);
            model.addAttribute("uri", uri);
            model.addAttribute("usuario", auth.getName());
            model.addAttribute("error", "Codigo invalido. Intente nuevamente.");
            return "usuarios/2fa-configurar";
        }

        actual.setSecretKey2fa(secret);
        usuarioService.actualizar2fa(actual);

        return "redirect:/usuarios/2fa/estado?exito";
    }

    @GetMapping("/2fa/estado")
    public String estado2fa(Model model, Authentication auth) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        boolean activo = actual.getSecretKey2fa() != null && !actual.getSecretKey2fa().isEmpty();
        model.addAttribute("activo", activo);
        model.addAttribute("exito", activo);
        return "usuarios/2fa-estado";
    }

    @GetMapping("/2fa/desactivar")
    public String desactivar2faForm(Model model, Authentication auth) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();
        if (actual.getSecretKey2fa() == null || actual.getSecretKey2fa().isEmpty()) {
            return "redirect:/usuarios/2fa/estado";
        }
        return "usuarios/2fa-desactivar";
    }

    @PostMapping("/2fa/desactivar")
    public String desactivar2fa(@RequestParam int codigo,
                                Authentication auth, Model model) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        if (actual.getSecretKey2fa() == null || actual.getSecretKey2fa().isEmpty()) {
            return "redirect:/usuarios/2fa/estado";
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean valido = gAuth.authorize(actual.getSecretKey2fa(), codigo);

        if (!valido) {
            model.addAttribute("error", "Codigo invalido. No se desactivo el 2FA.");
            return "usuarios/2fa-desactivar";
        }

        actual.setSecretKey2fa(null);
        usuarioService.actualizar2fa(actual);
        return "redirect:/usuarios/2fa/estado?desactivado";
    }

    @GetMapping("/2fa/verificar-sesion")
    public String verificarSesionForm(@RequestParam(required = false) String redirect, Model model) {
        model.addAttribute("redirect", redirect != null ? redirect : "/inicio");
        return "usuarios/2fa-verificar-sesion";
    }

    @PostMapping("/2fa/verificar-sesion")
    public String verificarSesion(@RequestParam int codigo,
                                  @RequestParam(defaultValue = "/inicio") String redirect,
                                  Authentication auth, HttpSession session, Model model) {
        Usuario actual = usuarioService.buscarPorUsuario(auth.getName()).orElseThrow();

        if (actual.getSecretKey2fa() == null || actual.getSecretKey2fa().isEmpty()) {
            return "redirect:" + redirect;
        }

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean valido = gAuth.authorize(actual.getSecretKey2fa(), codigo);

        if (!valido) {
            model.addAttribute("redirect", redirect);
            model.addAttribute("error", "Codigo invalido. Intente nuevamente.");
            return "usuarios/2fa-verificar-sesion";
        }

        session.setAttribute("2fa_verified", true);
        return "redirect:" + redirect;
    }
}
