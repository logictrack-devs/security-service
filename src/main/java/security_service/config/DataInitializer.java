package security_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import security_service.model.Rol;
import security_service.model.Usuario;
import security_service.repository.UsuarioRepository;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner inicializarAdmin(
            UsuarioRepository repo,
            PasswordEncoder encoder,
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.email:admin@logitrack.com}") String adminEmail,
            @Value("${app.admin.password:admin123}") String adminPassword
    ) {
        return args -> {
            if (!repo.existsByUsernameOrEmail(adminUsername, adminEmail)) {
                Usuario admin = new Usuario();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                admin.setPassword(encoder.encode(adminPassword));
                admin.setRol(Rol.ADMIN);
                repo.save(admin);
                log.info("==========================================================");
                log.info(">>> Usuario Administrador creado exitosamente: '{}' <<<", adminUsername);
                log.info("==========================================================");
            } else {
                log.info(">>> El usuario Administrador ya existe en la base de datos <<<");
            }
        };
    }
}
