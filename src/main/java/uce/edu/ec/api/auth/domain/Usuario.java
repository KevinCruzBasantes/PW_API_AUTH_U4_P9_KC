package uce.edu.ec.api.auth.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario   {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="usuario_seq")
    public  Long id;
    public String username;
    public String password;
    public String rol;
    
    
}