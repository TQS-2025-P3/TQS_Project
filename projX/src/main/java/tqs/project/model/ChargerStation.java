package tqs.project.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "charger_station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChargerStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // ← Nome único
    private String name;

    @Column(nullable = false)
    private double latitude;
    
    @Column(nullable = false)
    private double longitude;

    private int slots;

    @Column(name = "slots_in_use")
    private int slotsInUse;

    @Column(name = "price_per_kwh")
    private double pricePerKwh;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    @JsonBackReference
    private Staff staff;

    @Transient
    public int getAvailableSlots() {
        return slots - slotsInUse;
    }
}