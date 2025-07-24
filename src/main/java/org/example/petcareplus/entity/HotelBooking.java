package org.example.petcareplus.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petcareplus.entity.Enum.BookingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "HotelBookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelBookingId;

    @Column(nullable = false)
    private LocalDateTime bookDate;

    private String note;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_profile_id")
    private PetProfile petProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    //method thêm trong trường hợp lombok không hoạt động
    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PetProfile getPetProfile() {
        return petProfile;
    }

    public void setPetProfile(PetProfile petProfile) {
        this.petProfile = petProfile;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
