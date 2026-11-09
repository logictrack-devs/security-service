package security_service.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import security_service.model.Rol;
import security_service.model.Usuario;
import security_service.repository.UsuarioRepository;
import security_service.service.UsuarioService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioRepository repo;
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioRepository r, UsuarioService us) {
        this.repo = r;
        this.usuarioService = us;
    }

    public record CambiarRolRequest(@NotNull(message = "El rol es obligatorio") Rol rol) {}

    @GetMapping("/usuarios/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        var u = repo.findByUsername(jwt.getSubject()).orElseThrow();
        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "rol", u.getRol()
        );
    }

    @GetMapping("/admin/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @PutMapping("/admin/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> cambiarRol(@PathVariable Long id, @Valid @RequestBody CambiarRolRequest req) {
        var u = usuarioService.cambiarRol(id, req.rol());
        return Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "rol", u.getRol(),
                "mensaje", "Rol actualizado exitosamente"
        );
    }
}
