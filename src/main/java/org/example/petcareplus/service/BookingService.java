package org.example.petcareplus.service;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.enums.BookingStatus;
import org.example.petcareplus.enums.ServiceCategory;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    List<MyServiceDTO> getMyServices(Long accountId);

    List<MyServiceDTO> filterMyServices(Long profileId, String type, String status);

    boolean cancelBooking(String type, Long id, Long profileId);

    MyServiceDTO getBookingDetail(String type, Long id, Long profileId);

    void updateBooking(MyServiceDTO updated);

    boolean isPending(ServiceCategory category, Long id);
}
