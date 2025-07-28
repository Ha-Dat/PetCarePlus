package org.example.petcareplus.dto;

import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;

import java.time.LocalDateTime;

public class MyServiceDTO {

    private String petName;
    private String serviceName;
    private ServiceCategory serviceCategory;
    private LocalDateTime bookDate;
    private BookingStatus status;

    public MyServiceDTO() {
    }

    public MyServiceDTO(String petName, String serviceName, ServiceCategory serviceCategory, LocalDateTime bookDate, BookingStatus status) {
        this.petName = petName;
        this.serviceName = serviceName;
        this.serviceCategory = serviceCategory;
        this.bookDate = bookDate;
        this.status = status;
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
}
