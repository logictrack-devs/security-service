package security_service.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Rol {
    ADMIN, CLIENTE, REPARTIDOR;

    @JsonCreator
    public static Rol fromString(String value) {
        if (value == null) {
            return null;
        }
        for (Rol rol : Rol.values()) {
            if (rol.name().equalsIgnoreCase(value.trim())) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol inválido: " + value);
    }
}
