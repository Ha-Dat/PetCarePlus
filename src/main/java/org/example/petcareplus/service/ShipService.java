package org.example.petcareplus.service;

import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.Shipment;

import java.util.Map;

public interface ShipService {
    Map<String, Object> createShipment(Order order);

    Shipment saveFromGHNResponse(Order order, Map<String, Object> ghnResponse);
}
