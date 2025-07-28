package org.example.petcareplus.service;

import org.example.petcareplus.dto.MyServiceDTO;

import java.util.List;

public interface BookingService {

    List<MyServiceDTO> getMyServices(Long accountId);
}
