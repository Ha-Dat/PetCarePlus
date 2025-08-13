package org.example.petcareplus.controller;

import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.Prescription;
import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.enums.RequestType;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class ManagerController {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleRequestService workScheduleRequestService;

    public ManagerController(WorkScheduleService workScheduleService, WorkScheduleRequestService workScheduleRequestService) {
        this.workScheduleService = workScheduleService;
        this.workScheduleRequestService = workScheduleRequestService;
    }

    @GetMapping("/manager/shift-dashboards")
    public String showManagerPage(Model model) {
        // Lấy toàn bộ WorkSchedule
        List<WorkSchedule> schedules = workScheduleService.findAll();

        // Lấy toàn bộ WorkScheduleRequest
        List<WorkScheduleRequest> allRequests = workScheduleRequestService.findAll();

        // Lọc các request "Xin nghỉ"
        List<WorkScheduleRequest> absentRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.OFF)
                .collect(Collectors.toList());

        // Lọc các request "Đổi lịch"
        List<WorkScheduleRequest> shiftChangeRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.CHANGE)
                .collect(Collectors.toList());

        // Truyền vào view
        model.addAttribute("schedules", schedules);
        model.addAttribute("absentRequests", absentRequests);
        model.addAttribute("shiftChangeRequests", shiftChangeRequests);

        return "manager"; // manager.html
    }
}
