package uce.edu.ec.api.auth.aplication;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import uce.edu.ec.api.auth.domain.Usuario;
import uce.edu.ec.api.auth.infraestructure.UsuarioRepository;

@ApplicationScoped
public class UsuarioService {
    @Inject
    private UsuarioRepository usuarioRepository;

   public Usuario findByUsername(String username) {
        return usuarioRepository.find("username", username).firstResult();
    }
}
