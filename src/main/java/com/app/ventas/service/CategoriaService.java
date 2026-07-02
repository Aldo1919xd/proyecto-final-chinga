package com.app.ventas.service;

import com.app.ventas.entity.Categoria;
import com.app.ventas.entity.Usuario;
import com.app.ventas.repository.CategoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final AuditoriaService auditoriaService;

    public CategoriaService(CategoriaRepository categoriaRepository, AuditoriaService auditoriaService) {
        this.categoriaRepository = categoriaRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<Categoria> listarActivos() {
        return categoriaRepository.findByEstadoTrue();
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public Categoria guardar(Categoria categoria, Usuario usuarioActual, HttpServletRequest request) {
        boolean esNuevo = categoria.getCodCategoria() == null;
        Categoria guardado = categoriaRepository.save(categoria);
        auditoriaService.registrar(usuarioActual, "Maestras", "Categoria",
                esNuevo ? "INSERT" : "UPDATE",
                guardado.getCodCategoria(),
                esNuevo ? null : "{\"nombre\":\"" + categoria.getNombreCategoria() + "\"}",
                "{\"nombre\":\"" + guardado.getNombreCategoria() + "\"}",
                request);
        return guardado;
    }

    @Transactional
    public void eliminarLogico(Integer id, Usuario usuarioActual, HttpServletRequest request) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow();
        categoria.setEstado(false);
        categoriaRepository.save(categoria);
        auditoriaService.registrar(usuarioActual, "Maestras", "Categoria",
                "DELETE", id, "{\"estado\":true}", "{\"estado\":false}", request);
    }
}
