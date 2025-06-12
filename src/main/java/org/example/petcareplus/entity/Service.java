package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer serviceId;

    private String name;
    private String serviceCategory;
    private BigDecimal price;
    private Integer duration;

    @OneToMany(mappedBy = "service")
    private List<AppointmentBooking> appointmentBookings;

    @OneToMany(mappedBy = "service")
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "service")
    private List<SpaBooking> spaBookings;
}
