package security_service.service;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import security_service.model.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
public class JwtService {
    private final JwtEncoder encoder;
    public JwtService(JwtEncoder encoder) { this.encoder = encoder; }
    public String generarToken(Usuario u) {
        var claims = JwtClaimsSet.builder()
                .issuer("security-service")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(2, ChronoUnit.HOURS))
                .subject(u.getUsername())
                .claim("roles", List.of("ROLE_" + u.getRol().name()))
                .claim("email", u.getEmail())
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
