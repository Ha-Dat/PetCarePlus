package org.example.petcareplus.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.dto.WorkScheduleDTO;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.enums.RequestType;
import org.example.petcareplus.enums.ScheduleStatus;
import org.example.petcareplus.enums.Shift;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ManagerController {
    private final WorkScheduleService workScheduleService;
    private final WorkScheduleRequestService workScheduleRequestService;
    private final AccountService accountService;

    public ManagerController(WorkScheduleService workScheduleService, WorkScheduleRequestService workScheduleRequestService, AccountService accountService) {
        this.workScheduleService = workScheduleService;
        this.workScheduleRequestService = workScheduleRequestService;
        this.accountService = accountService;
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
                        "accountId", r.getAccount().getAccountId().toString(),
                        "accountName", r.getAccount().getName(),
                        "accountPhone", r.getAccount().getPhone(),
                        "accountRole", r.getAccount().getRole().getValue(),
                        "scheduleId", r.getScheduleId().toString(),
                        "workDate", r.getWorkDate().toString(),
                        "shift", r.getShift().getValue(),
                        "shiftName", r.getShift().name(),
                        "note", r.getNote(),
                        "statusName", r.getStatus().name()
                ))
                .collect(Collectors.toList());

        model.addAttribute("shift", Shift.values());
        model.addAttribute("scheduleStatus", ScheduleStatus.values());
        model.addAttribute("schedules", schedules);
        model.addAttribute("absentRequests", absentRequests);
        model.addAttribute("shiftChangeRequests", shiftChangeRequests);

        return "manager.html";
    }

    private WorkSchedule convertToSchedule(WorkScheduleDTO dto, WorkSchedule existingSchedule) {
        existingSchedule.setNote(dto.getNote());
        existingSchedule.setStatus(dto.getStatus());
        existingSchedule.setWorkDate(dto.getWorkDate());
        existingSchedule.setShift(dto.getShift());
        accountService.getById(dto.getAccountId())
                .ifPresentOrElse(
                        existingSchedule::setAccount,
                        () -> { throw new IllegalArgumentException("Account not found for ID: " + dto.getAccountId()); }
                );
        return existingSchedule;
    }

    @PutMapping(value = "/manager-dashboard/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody WorkScheduleDTO dto) {
        try {
            Optional<WorkSchedule> existing = workScheduleService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            WorkSchedule updatedSchedule = convertToSchedule(dto, existing.get());
            updatedSchedule.setScheduleId(id);
            workScheduleService.save(updatedSchedule);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }
}
