package org.example.petcareplus.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.petcareplus.dto.ProductDTO;
import org.example.petcareplus.dto.WorkScheduleDTO;
import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.entity.Product;
import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.enums.*;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
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

        // Get available staff for scheduling
        List<Map<String, String>> availableStaff = accountService.findByRoleIn(Arrays.asList(AccountRole.PET_GROOMER, AccountRole.SELLER, AccountRole.VET))
                .stream()
                .map(account -> Map.of(
                        "accountId", account.getAccountId().toString(),
                        "accountName", account.getName(),
                        "accountPhone", account.getPhone(),
                        "accountRole", account.getRole().getValue(),
                        "roleName", account.getRole().name()
                ))
                .collect(Collectors.toList());

        List<Map<String, String>> absentRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.OFF && r.getStatus() == ScheduleRequestStatus.PENDING)
                .map(r -> Map.of(
                        "requestId", r.getRequestId().toString(),
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
                .filter(r -> r.getRequestType() == RequestType.CHANGE && r.getStatus() == ScheduleRequestStatus.PENDING)
                .map(r -> Map.of(
                        "requestId", r.getRequestId().toString(),
                        "accountName", r.getOriginalSchedule().getAccount().getName(),
                        "accountPhone", r.getOriginalSchedule().getAccount().getPhone(),
                        "accountRole", r.getOriginalSchedule().getAccount().getRole().getValue(),
                        "workDate", r.getOriginalSchedule().getWorkDate().toString(),
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
        model.addAttribute("availableStaff", availableStaff);

        return "manager";
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

            // Check if the new work date is in the past
            java.time.LocalDate today = java.time.LocalDate.now();
            if (dto.getWorkDate().isBefore(today)) {
                return ResponseEntity.badRequest().body("Cannot update schedule to a past date");
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

    @PostMapping("/manager-dashboard/approve/{id}")
    public String approveRequest(@PathVariable("id") Long id) {
        Optional<WorkScheduleRequest> existing = workScheduleRequestService.findById(id);
        if (existing.isPresent()) {
            WorkScheduleRequest sche = existing.get();
            sche.setStatus(ScheduleRequestStatus.APPROVED);
            WorkSchedule workSchedule = workScheduleService.findById(sche.getOriginalSchedule().getScheduleId()).get();
            if(sche.getRequestType() == RequestType.CHANGE) {
                workSchedule.setWorkDate(sche.getRequestedDate());
                workSchedule.setShift(sche.getRequestedShift());
            } else {
                workSchedule.setStatus(ScheduleStatus.LEAVE_APPROVED);
            }
            workScheduleService.save(workSchedule);
            workScheduleRequestService.save(sche);
            return "redirect:/manager/manager-dashboard"; // This tells Spring to redirect
        }
        return "redirect:/manager/manager-dashboard?error=true"; // or some error page
    }

    @PostMapping("/manager-dashboard/disapprove/{id}")
    public String disapproveRequest(@PathVariable("id") Long id) {
        Optional<WorkScheduleRequest> existing = workScheduleRequestService.findById(id);
        if (existing.isPresent()) {
            WorkScheduleRequest sche = existing.get();
            sche.setStatus(ScheduleRequestStatus.REJECTED);
            workScheduleRequestService.save(sche);
            return "redirect:/manager/manager-dashboard";
        }
        return "redirect:/manager/manager-dashboard?error=true";
    }

    @PostMapping("/manager-dashboard/create-schedule")
    @ResponseBody
    public ResponseEntity<?> createSchedule(@RequestBody WorkScheduleDTO dto) {
        try {
            WorkSchedule newSchedule = new WorkSchedule();
            newSchedule.setNote(dto.getNote());
            newSchedule.setStatus(ScheduleStatus.PENDING);
            newSchedule.setWorkDate(dto.getWorkDate());
            newSchedule.setShift(dto.getShift());
            
            accountService.getById(dto.getAccountId())
                    .ifPresentOrElse(
                            newSchedule::setAccount,
                            () -> { throw new IllegalArgumentException("Account not found for ID: " + dto.getAccountId()); }
                    );
            
            workScheduleService.save(newSchedule);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to create schedule: " + e.getMessage());
        }
    }

    @DeleteMapping("/manager-dashboard/delete-schedule/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            Optional<WorkSchedule> existing = workScheduleService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            WorkSchedule schedule = existing.get();
            
            // Check if the schedule is in the past
            java.time.LocalDate today = java.time.LocalDate.now();
            if (schedule.getWorkDate().isBefore(today)) {
                return ResponseEntity.badRequest().body("Cannot delete past schedules");
            }

            workScheduleService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to delete schedule: " + e.getMessage());
        }
    }
}
