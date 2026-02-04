package uce.edu.ec.api.auth.interfaces;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import uce.edu.ec.api.auth.domain.Usuario;

import java.net.URI;
import java.time.Duration;
import java.util.*;

@Path("/oauth")
public class AuthResource {
    
    private static final Map<String, String> authCodes = new HashMap<>();
    
  
    @GET
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorizeInfo() {
        return Response.ok(Map.of(
            "message", "Para autorizar, envíe un POST a /oauth/login con sus credenciales",
            "required_fields", List.of("username", "password", "redirect_uri")
        )).build();
    }
    
   
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("redirect_uri") String redirectUri) {
        
        Usuario usuario = Usuario.findByUsername(username);
        
        if (usuario == null || !usuario.password.equals(password)) {
            return Response.status(401)
                    .entity(Map.of("error", "Credenciales inválidas"))
                    .build();
        }
        
        // Generar código
        String code = UUID.randomUUID().toString();
        authCodes.put(code, username);
        
        String redirectUrl = redirectUri + "?code=" + code;
        return Response.seeOther(URI.create(redirectUrl)).build();
    }
    
   
    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(
            @FormParam("grant_type") String grantType,
            @FormParam("code") String code,
            @FormParam("client_id") String clientId) {
        
        String username = authCodes.get(code);
        if (username == null) {
            return Response.status(400)
                    .entity(Map.of("error", "invalid_grant", "message", "Código inválido"))
                    .build();
        }
        
        authCodes.remove(code);
        Usuario usuario = Usuario.findByUsername(username);
        
        String token = Jwt.issuer("matricula-auth")
                .subject(usuario.username)
                .groups(Set.of(usuario.rol))
                .expiresIn(Duration.ofHours(1))
                .sign();
        
        return Response.ok(Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "expires_in", 3600
        )).build();
    }
}