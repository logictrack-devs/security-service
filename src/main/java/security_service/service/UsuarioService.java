package security_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import security_service.model.Rol;
import security_service.model.Usuario;
import security_service.repository.UsuarioRepository;

@Service
public class UsuarioService {
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository r, PasswordEncoder e) {
        this.repo = r;
        this.encoder = e;
    }

    public Usuario registrar(String username, String email, String rawPassword, Rol rol) {
        // 1. RESTRICCIÓN: No se permite registrar el rol ADMIN públicamente
        if (rol == Rol.ADMIN) {
            throw new IllegalArgumentException("No se permite el registro de usuarios con el rol ADMIN");
        }
        // 2. RESTRICCIÓN: No se permite registrar el username admin o administrador
        if (username != null && ("admin".equalsIgnoreCase(username.trim()) || "administrador".equalsIgnoreCase(username.trim()))) {
            throw new IllegalArgumentException("No se permite registrar el usuario administrador");
        }
        // 3. Verificación de existencia previa
        if (repo.existsByUsernameOrEmail(username, email)) {
            throw new IllegalArgumentException("Usuario o email ya registrado");
        }

        var u = new Usuario();
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(encoder.encode(rawPassword)); // hash BCrypt
        u.setRol(rol == null ? Rol.CLIENTE : rol);
        return repo.save(u);
    }

    public Usuario cambiarRol(Long id, Rol nuevoRol) {
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El nuevo rol no puede ser nulo");
        }
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        u.setRol(nuevoRol);
        return repo.save(u);
    }
}
