package org.example.petcareplus.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.petcareplus.dto.CategorySalesDTO;
import org.example.petcareplus.dto.MonthlyRevenueDTO;
import org.example.petcareplus.entity.Order;
import org.example.petcareplus.service.HotelBookingService;
import org.example.petcareplus.service.OrderService;
import org.example.petcareplus.service.ProductService;
import org.example.petcareplus.service.SpaBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping()
public class StatisticsController {

    private final HotelBookingService hotelBookingService;
    private final SpaBookingService spaBookingService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public StatisticsController(HotelBookingService hotelBookingService,
                                SpaBookingService spaBookingService,
                                ProductService productService,
                                OrderService orderService) {
        this.hotelBookingService = hotelBookingService;
        this.spaBookingService = spaBookingService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/pet-groomer/dashboard")
    public String getMonthlyBookingCount(Model model) throws JsonProcessingException {
        // Dữ liệu booking khách sạn
        List<Object[]> hotelDataRaw = hotelBookingService.getBookingCountByMonthInCurrentYear();
        // Dữ liệu booking spa
        List<Object[]> spaDataRaw = spaBookingService.getBookingCountByMonthInCurrentYear();

        // Map để lưu month -> count
        Map<String, Integer> hotelMap = hotelDataRaw.stream()
                .collect(Collectors.toMap(
                        obj -> String.valueOf(obj[0]),
                        obj -> ((Number) obj[1]).intValue()
                ));

        Map<String, Integer> spaMap = spaDataRaw.stream()
                .collect(Collectors.toMap(
                        obj -> String.valueOf(obj[0]),
                        obj -> ((Number) obj[1]).intValue()
                ));

        // Tạo mảng 2 chiều cho chart
        List<List<Object>> chartData = new ArrayList<>();
        chartData.add(Arrays.asList("Tháng", "Đơn Khách Thú Cưng", "Đơn Spa Thú Cưng")); // header

        for (int i = 1; i <= 12; i++) {
            String month = String.valueOf(i);
            chartData.add(Arrays.asList(
                    month,
                    hotelMap.getOrDefault(month, 0),
                    spaMap.getOrDefault(month, 0)
            ));
        }

        ObjectMapper mapper = new ObjectMapper();
        model.addAttribute("chartDataJson", mapper.writeValueAsString(chartData));
        model.addAttribute("totalHotelBookings", hotelBookingService.getTotalHotelBookings());
        model.addAttribute("totalSpaBookings", spaBookingService.getTotalSpaBookings());
        model.addAttribute("totalPendingBookings", hotelBookingService.getTotalPendingHotelBooking() + spaBookingService.getTotalPendingSpaBooking());
        model.addAttribute("totalAcceptedBookings", hotelBookingService.getTotalAcceptedHotelBookings() + spaBookingService.getTotalAcceptedSpaBookings());

        return "groomer-dashboard";
    }

    @GetMapping("/test")
    public String getSellerDashboard(Model model) {
        List<CategorySalesDTO> stats = productService.getTotalSoldByEachParentCategory();
        model.addAttribute("stats", stats);
        List<MonthlyRevenueDTO> revenue = orderService.getMonthlyRevenue();
        model.addAttribute("revenue", revenue);
        model.addAttribute("totalUnitsInStock", productService.getTotalUnitsInStock());
        model.addAttribute("totalUnitsSold", productService.getTotalUnitsSold());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        return "seller-dashboard";
    }

}
