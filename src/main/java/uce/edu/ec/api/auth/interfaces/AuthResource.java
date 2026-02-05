package uce.edu.ec.api.auth.interfaces;

import java.time.Instant;
import java.util.Set;
 
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import uce.edu.ec.api.auth.aplication.UsuarioService;
import uce.edu.ec.api.auth.domain.Usuario;

@Path("/auth")
public class AuthResource {

    @Inject
    private UsuarioService usuarioService;
    // Inyectamos los valores del application.properties
    @ConfigProperty(name = "auth.issuer")
    String issuer;

    @ConfigProperty(name = "auth.token.ttl")
    Long ttl;
    @GET
@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
public TokenResponse token(
        @QueryParam("username") String username, // Cambiado para coincidir con Postman y la BD
        @QueryParam("password") String password) {

    // Ahora pasamos la variable 'username' que recibimos del QueryParam
    Usuario usuario = usuarioService.findByUsername(username);
    
    if (usuario != null && usuario.password.equals(password)) {
        
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttl);

        String jwt = Jwt.issuer(issuer) 
                .subject(usuario.username)
                .groups(Set.of(usuario.rol))
                .issuedAt(now)
                .expiresAt(exp)
                .sign();

        return new TokenResponse(jwt, exp.getEpochSecond(), usuario.rol);
    } else {
        // Esto lanzará el error 401 que viste en Postman, pero con un mensaje claro
        throw new WebApplicationException("Credenciales incorrectas", 401);
    }
}
    // Clase interna para la respuesta JSON
    public static class TokenResponse {
        public String accessToken;
        public long expiresAt;
        public String role;

        public TokenResponse() {}

        public TokenResponse(String accessToken, long expiresAt, String role) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.role = role;
        }
    }
}