package tqs.project.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

    private String name;
    
    @Column(unique = true, nullable = false) // ← Email único
    private String email;
    
    private String password;
}