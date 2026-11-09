package security_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import security_service.model.Rol;
import security_service.repository.UsuarioRepository;
import security_service.service.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final UsuarioRepository repo;
    private final AuthenticationManager authManager;

    public AuthController(UsuarioService us, JwtService js,
                          UsuarioRepository r, AuthenticationManager am) {
        this.usuarioService = us;
        this.jwtService = js;
        this.repo = r;
        this.authManager = am;
    }

    public record RegisterRequest(
            @NotBlank String username,
            @Email @NotBlank String email,
            @Size(min = 6) @NotBlank String password,
            Rol rol
    ) {}

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record AuthResponse(String token, String type, long expiresIn) {}

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        Rol rol = req.rol() != null ? req.rol() : Rol.CLIENTE;
        var u = usuarioService.registrar(req.username(), req.email(),
                req.password(), rol);
        return new AuthResponse(jwtService.generarToken(u), "Bearer", 7200);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        var u = repo.findByUsername(req.username()).orElseThrow();
        return new AuthResponse(jwtService.generarToken(u), "Bearer", 7200);
    }
}
