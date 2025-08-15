package org.example.petcareplus.dto;

import lombok.NoArgsConstructor;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;

import java.time.LocalDateTime;

@NoArgsConstructor
public class MyServiceDTO {

    private Long bookingId;
    private String petName;
    private String serviceName;
    private ServiceCategory serviceCategory;
    private LocalDateTime bookDate;
    private BookingStatus status;
    private String note;

    public MyServiceDTO(Long bookingId, String petName, String serviceName, ServiceCategory serviceCategory, LocalDateTime bookDate, BookingStatus status, String note) {
        this.bookingId = bookingId;
        this.petName = petName;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.bookDate = bookDate;
        this.status = status;
        this.note = note;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public LocalDateTime getBookDate() {
        return bookDate;
    }

    public void setBookDate(LocalDateTime bookDate) {
        this.bookDate = bookDate;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
