package uce.edu.ec.api.auth.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario extends PanacheEntity {
    
    public String username;
    public String password;
    public String rol;
    
    public static Usuario findByUsername(String username) {
        return find("username", username).firstResult();
    }
}