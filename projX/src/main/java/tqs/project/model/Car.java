package tqs.project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private String plate;
    private double batteryCapacity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User owner;
}
