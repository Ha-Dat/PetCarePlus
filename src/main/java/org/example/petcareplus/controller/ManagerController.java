package org.example.petcareplus.controller;

import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.enums.RequestType;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ManagerController {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleRequestService workScheduleRequestService;

    public ManagerController(WorkScheduleService workScheduleService, WorkScheduleRequestService workScheduleRequestService) {
        this.workScheduleService = workScheduleService;
        this.workScheduleRequestService = workScheduleRequestService;
    }

    @GetMapping("/manager-dashboard")
    public String showManagerPage(Model model) {
        // Get all data from services
        List<WorkSchedule> allSchedules = workScheduleService.findAll();
        List<WorkScheduleRequest> allRequests = workScheduleRequestService.findAll();


        List<Map<String, String>> absentRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.OFF)
                .map(r -> Map.of(
                        "accountName", r.getOriginalSchedule().getAccount().getName(),
                        "accountPhone", r.getOriginalSchedule().getAccount().getPhone(),
                        "accountRole", r.getOriginalSchedule().getAccount().getRole().getValue(),
                        "workDate", r.getOriginalSchedule().getWorkDate().toString(),
                        "shift", r.getOriginalSchedule().getShift().getValue(),
                        "shiftName", r.getOriginalSchedule().getShift().name(),
                        "status", r.getStatus().getValue(),
                        "reason", r.getReason()
                ))
                .collect(Collectors.toList());

        List<Map<String, String>> shiftChangeRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.CHANGE)
                .map(r -> Map.of(
                        "accountName", r.getOriginalSchedule().getAccount().getName(),
                        "accountPhone", r.getOriginalSchedule().getAccount().getPhone(),
                        "accountRole", r.getOriginalSchedule().getAccount().getRole().getValue(),
                        "workDate", r.getOriginalSchedule().getWorkDate().toString(),
                        "shift", r.getOriginalSchedule().getShift().getValue(),
                        "shiftName", r.getOriginalSchedule().getShift().name(),
                        "status", r.getStatus().getValue(),
                        "reason", r.getReason(),
                        "requestDate", r.getRequestedDate().toString(),
                        "requestShift", r.getRequestedShift().toString()
                ))
                .collect(Collectors.toList());

        List<Map<String, String>> schedules = allSchedules.stream()
                .map(r -> Map.of(
                        "accountName", r.getAccount().getName(),
                        "accountPhone", r.getAccount().getPhone(),
                        "accountRole", r.getAccount().getRole().getValue(),
                        "workDate", r.getWorkDate().toString(),
                        "shift", r.getShift().getValue(),
                        "shiftName", r.getShift().name(),
                        "note", r.getNote(),
                        "status", r.getStatus().getValue()
                ))
                .collect(Collectors.toList());


        model.addAttribute("schedules", schedules);
        model.addAttribute("absentRequests", absentRequests);
        model.addAttribute("shiftChangeRequests", shiftChangeRequests);

        return "manager.html";
    }
}
