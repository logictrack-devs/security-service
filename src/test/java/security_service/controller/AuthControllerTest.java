package security_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import security_service.exception.ManejadorExcepcionesGlobal;
import security_service.model.Rol;
import security_service.model.Usuario;
import security_service.repository.UsuarioRepository;
import security_service.service.JwtService;
import security_service.service.UsuarioService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new ManejadorExcepcionesGlobal())
                .build();
    }

    @Test
    @DisplayName("POST /api/auth/register con rol ADMIN devuelve 400 Bad Request")
    void register_ConRolAdmin_DevuelveBadRequest() throws Exception {
        when(usuarioService.registrar(any(), any(), any(), eq(Rol.ADMIN)))
                .thenThrow(new IllegalArgumentException("No se permite el registro de usuarios con el rol ADMIN"));

        String json = """
                {
                    "username": "usuario_test",
                    "email": "test@example.com",
                    "password": "password123",
                    "rol": "ADMIN"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("No se permite el registro de usuarios con el rol ADMIN"));
    }

    @Test
    @DisplayName("POST /api/auth/register con username admin devuelve 400 Bad Request")
    void register_ConUsernameAdmin_DevuelveBadRequest() throws Exception {
        when(usuarioService.registrar(eq("admin"), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("No se permite registrar el usuario administrador"));

        String json = """
                {
                    "username": "admin",
                    "email": "admin@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("No se permite registrar el usuario administrador"));
    }

    @Test
    @DisplayName("POST /api/auth/register con datos válidos devuelve 201 Created")
    void register_Valido_DevuelveCreated() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("nuevo_usuario");
        usuario.setEmail("nuevo@example.com");
        usuario.setRol(Rol.CLIENTE);

        when(usuarioService.registrar(eq("nuevo_usuario"), eq("nuevo@example.com"), eq("password123"), eq(Rol.CLIENTE)))
                .thenReturn(usuario);
        when(jwtService.generarToken(usuario)).thenReturn("mock.jwt.token");

        String json = """
                {
                    "username": "nuevo_usuario",
                    "email": "nuevo@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }
}
