-- Inserta el usuario administrador solo si no existe.
-- La contraseña 'admin123' está cifrada con BCrypt.
INSERT INTO usuarios (username, email, password, rol)
SELECT 'admin', 'admin@logitrack.com',
       '$2a$10$GKznUmJ2xWjfsoMi1WL12.5PWzT76LRVSx1ndWWs/qvXvE6Rex96S',
       'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM usuarios WHERE username = 'admin'
);
