package security_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import security_service.model.Rol;
import security_service.model.Usuario;
import security_service.repository.UsuarioRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar registrar un usuario con rol ADMIN")
    void registrar_ConRolAdmin_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrar("juan", "juan@test.com", "password123", Rol.ADMIN)
        );

        assertEquals("No se permite el registro de usuarios con el rol ADMIN", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar registrar con username 'admin'")
    void registrar_ConUsernameAdmin_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrar("admin", "admin@test.com", "password123", Rol.CLIENTE)
        );

        assertEquals("No se permite registrar el usuario administrador", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar registrar con username 'administrador'")
    void registrar_ConUsernameAdministrador_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrar("administrador", "admin@test.com", "password123", Rol.CLIENTE)
        );

        assertEquals("No se permite registrar el usuario administrador", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario o email ya existe")
    void registrar_UsuarioOEmailExistente_LanzaExcepcion() {
        when(usuarioRepository.existsByUsernameOrEmail("juan", "juan@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.registrar("juan", "juan@test.com", "password123", Rol.CLIENTE)
        );

        assertEquals("Usuario o email ya registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe registrar exitosamente un usuario con rol CLIENTE")
    void registrar_ConRolCliente_Exitoso() {
        when(usuarioRepository.existsByUsernameOrEmail("juan", "juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrar("juan", "juan@test.com", "password123", Rol.CLIENTE);

        assertNotNull(resultado);
        assertEquals("juan", resultado.getUsername());
        assertEquals("juan@test.com", resultado.getEmail());
        assertEquals("encodedPassword", resultado.getPassword());
        assertEquals(Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe asignar rol CLIENTE por defecto si el rol es null")
    void registrar_ConRolNull_AsignaRolCliente() {
        when(usuarioRepository.existsByUsernameOrEmail("juan", "juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrar("juan", "juan@test.com", "password123", null);

        assertNotNull(resultado);
        assertEquals(Rol.CLIENTE, resultado.getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe registrar exitosamente un usuario con rol REPARTIDOR")
    void registrar_ConRolRepartidor_Exitoso() {
        when(usuarioRepository.existsByUsernameOrEmail("pedro", "pedro@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrar("pedro", "pedro@test.com", "password123", Rol.REPARTIDOR);

        assertNotNull(resultado);
        assertEquals("pedro", resultado.getUsername());
        assertEquals(Rol.REPARTIDOR, resultado.getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe cambiar el rol de un usuario existente exitosamente")
    void cambiarRol_UsuarioExistente_Exitoso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("juan");
        usuario.setRol(Rol.CLIENTE);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.cambiarRol(1L, Rol.REPARTIDOR);

        assertNotNull(resultado);
        assertEquals(Rol.REPARTIDOR, resultado.getRol());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar excepción al cambiar rol si el usuario no existe")
    void cambiarRol_UsuarioNoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> usuarioService.cambiarRol(99L, Rol.ADMIN)
        );

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, never()).save(any());
    }
}
