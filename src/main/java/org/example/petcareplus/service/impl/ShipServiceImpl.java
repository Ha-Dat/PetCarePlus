package org.example.petcareplus.service.impl;

import org.example.petcareplus.entity.Order;
import org.example.petcareplus.entity.Shipment;
import org.example.petcareplus.enums.ShippingStatus;
import org.example.petcareplus.repository.ShipRepository;
import org.example.petcareplus.service.ShipService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    @Value("${ghn.api.base-url}")
    private String baseUrl;

    @Value("${ghn.api.token}")
    private String token;

    @Value("${ghn.api.shop-id}")
    private String shopId;

    private final RestTemplate restTemplate;

    public ShipServiceImpl(ShipRepository shipRepository, RestTemplateBuilder builder) {
        this.shipRepository = shipRepository;
        this.restTemplate = builder.build();
    }

    public Map<String, Object> createShipment(Order order) {
        String url = baseUrl + "/v2/shipping-order/create";

        Map<String, Object> payload = new HashMap<>();
        payload.put("shop_id", String.valueOf(Integer.parseInt(shopId)));
        payload.put("to_name", order.getReceiverName());
        payload.put("to_phone", order.getReceiverPhone());
        payload.put("to_address", order.getDeliverAddress());
        payload.put("to_ward_code", 1450);
        payload.put("to_district_id", "21234");
        payload.put("weight", 500);
        payload.put("length", 20);
        payload.put("width", 20);
        payload.put("height", 10);
        payload.put("service_type_id", 2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", shopId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        return response.getBody();
    }

    @Override
    public Shipment saveFromGHNResponse(Order order, Map<String, Object> ghnResponse) {
        Map<String, Object> data = (Map<String, Object>) ghnResponse.get("data");

        Shipment shipment = new Shipment();
        shipment.setOrder(order);
        shipment.setShipmentCode((String) data.get("order_code"));
        shipment.setShippingFee(new BigDecimal(data.get("total_fee").toString()));
        shipment.setServiceType("GHN");
        shipment.setExpectedDeliveryTime(LocalDateTime.now().plusDays(3)); // tạm fix
        shipment.setStatus(ShippingStatus.DELIVERING);
        shipment.setTrackingUrl("https://donhang.ghn.vn/?order_code=" + data.get("order_code"));
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        return shipRepository.save(shipment);
    }
}
