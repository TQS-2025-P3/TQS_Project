package tqs.project.model;

import jakarta.persistence.*;
import lombok.*;
import tqs.project.model.enums.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "book_charge")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;
    private double cost;
  

    private int duration;  
    @Enumerated(EnumType.STRING)
    private BookingStatus status; 

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private ChargerStation chargerStation;
}
