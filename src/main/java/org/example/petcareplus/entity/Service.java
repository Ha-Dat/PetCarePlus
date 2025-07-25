package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String serviceCategory;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer duration;

    @OneToMany(mappedBy = "service")
    private List<AppointmentBooking> appointmentBookings;

    @OneToMany(mappedBy = "service")
    private List<HotelBooking> hotelBookings;

    @OneToMany(mappedBy = "service")
    private List<SpaBooking> spaBookings;

    //method thêm trong trường hợp lombok không hoạt động
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<AppointmentBooking> getAppointmentBookings() {
        return appointmentBookings;
    }

    public void setAppointmentBookings(List<AppointmentBooking> appointmentBookings) {
        this.appointmentBookings = appointmentBookings;
    }

    public List<HotelBooking> getHotelBookings() {
        return hotelBookings;
    }

    public void setHotelBookings(List<HotelBooking> hotelBookings) {
        this.hotelBookings = hotelBookings;
    }

    public List<SpaBooking> getSpaBookings() {
        return spaBookings;
    }

    public void setSpaBookings(List<SpaBooking> spaBookings) {
        this.spaBookings = spaBookings;
    }
}
