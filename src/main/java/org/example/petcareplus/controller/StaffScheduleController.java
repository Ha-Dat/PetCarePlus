package org.example.petcareplus.controller;

import jakarta.servlet.http.HttpSession;
import org.example.petcareplus.dto.ChangeRequestDTO;
import org.example.petcareplus.dto.OffRequestDTO;
import org.example.petcareplus.dto.WorkScheduleDTO;
import org.example.petcareplus.entity.Account;
import org.example.petcareplus.entity.WorkSchedule;
import org.example.petcareplus.entity.WorkScheduleRequest;
import org.example.petcareplus.enums.*;
import org.example.petcareplus.service.AccountService;
import org.example.petcareplus.service.WorkScheduleRequestService;
import org.example.petcareplus.service.WorkScheduleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class StaffScheduleController {
    private AccountService accountService;
    private WorkScheduleService workScheduleService;
    private WorkScheduleRequestService workScheduleRequestService;

    public StaffScheduleController(AccountService accountService, WorkScheduleService workScheduleService, WorkScheduleRequestService workScheduleRequestService) {
        this.accountService = accountService;
        this.workScheduleService = workScheduleService;
        this.workScheduleRequestService = workScheduleRequestService;
    }

    @GetMapping("/staff/work-schedule")
    public String sellerWorkSchedule(Model model, HttpSession session,@RequestParam(required = false) String from) {
        Account account = (Account) session.getAttribute("loggedInUser");
        if(account.getRole().equals(AccountRole.CUSTOMER) || account.getRole().equals(AccountRole.ADMIN) || account.getRole().equals(AccountRole.MANAGER)) {
            return "redirect:/home";
        }
        List<WorkSchedule> allSchedules = workScheduleService.findAll().stream().filter(a -> a.getAccount().getAccountId().equals(account.getAccountId())).toList();
        List<WorkScheduleRequest> allRequests = workScheduleRequestService.findAll().stream().filter(a -> a.getOriginalSchedule().getAccount().getAccountId().equals(account.getAccountId())).toList();


        List<Map<String, String>> absentRequests = allRequests.stream()
                .filter(r -> r.getRequestType() == RequestType.OFF)
                .map(r -> Map.of(
                        "requestId", r.getRequestId().toString(),
                        "accountId",r.getOriginalSchedule().getAccount().getAccountId().toString(),
                        "scheduleId", r.getOriginalSchedule().getScheduleId().toString(),
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
                .filter(r -> r.getRequestType() == RequestType.CHANGE && r.getStatus() != ScheduleRequestStatus.APPROVED)
                .map(r -> Map.of(
                        "requestId", r.getRequestId().toString(),
                        "accountId",r.getOriginalSchedule().getAccount().getAccountId().toString(),
                        "scheduleId", r.getOriginalSchedule().getScheduleId().toString(),
                        "accountRole", r.getOriginalSchedule().getAccount().getRole().getValue(),
                        "workDate", r.getOriginalSchedule().getWorkDate().toString(),
                        "shiftName", r.getOriginalSchedule().getShift().name(),
                        "status", r.getStatus().getValue(),
                        "reason", r.getReason(),
                        "requestedDate", r.getRequestedDate().toString(),
                        "requestedShiftName", r.getRequestedShift().name()
                ))
                .collect(Collectors.toList());

        List<Map<String, String>> schedules = allSchedules.stream()
                .map(r -> Map.of(
                        "accountId", r.getAccount().getAccountId().toString(),
                        "accountName", r.getAccount().getName(),
                        "status", r.getStatus().getValue(),
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
        model.addAttribute("from", from);

        return "schedule";
    }


    @PutMapping(value = "/staff/work-schedule/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOffRequest(@PathVariable Long id, @RequestBody OffRequestDTO dto) {
        try {
            Optional<WorkScheduleRequest> existing = workScheduleRequestService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            WorkScheduleRequest updatedOffRequest = existing.get();
            updatedOffRequest.setReason(dto.getReason());

            workScheduleRequestService.save(updatedOffRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }

    @PutMapping(value = "/staff/work-schedule/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> CreateOffRequest(@RequestBody OffRequestDTO dto) {
        try {
            Optional<WorkSchedule> existing = workScheduleService.findById(dto.getScheduleId());
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            WorkScheduleRequest OffRequest = new WorkScheduleRequest();
            OffRequest.setOriginalSchedule(existing.get());
            OffRequest.setRequestType(RequestType.OFF);
            OffRequest.setReason(dto.getReason());
            OffRequest.setStatus(ScheduleRequestStatus.PENDING);

            workScheduleRequestService.save(OffRequest);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }

    @PostMapping(value = "/staff/work-schedule/delete/{id}")
    public String deleteOffRequest(@PathVariable Long id) {
        Optional<WorkScheduleRequest> existing = workScheduleRequestService.findById(id);
        if (existing.isEmpty()) {
            return "redirect:/work-schedule?error";
        }
        workScheduleRequestService.deleteById(existing.get().getRequestId());
        return "redirect:/work-schedule";
    }

    @PutMapping(value = "/staff/work-schedule/update-change-request/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateChangeRequest(@PathVariable Long id, @RequestBody ChangeRequestDTO dto) {
        try {
            Optional<WorkScheduleRequest> existing = workScheduleRequestService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            WorkScheduleRequest updatedChangeRequest = existing.get();
            updatedChangeRequest.setReason(dto.getReason());
            updatedChangeRequest.setRequestedDate(dto.getRequestedDate());
            updatedChangeRequest.setRequestedShift(dto.getRequestedShift());

            workScheduleRequestService.save(updatedChangeRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }

    @PutMapping(value = "/staff/work-schedule/create-change-request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> CreateChangeRequest(@RequestBody ChangeRequestDTO dto) {
        try {
            Optional<WorkSchedule> existing = workScheduleService.findById(dto.getScheduleId());
            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            WorkScheduleRequest ChangeRequest = new WorkScheduleRequest();
            ChangeRequest.setOriginalSchedule(existing.get());
            ChangeRequest.setRequestType(RequestType.CHANGE);
            ChangeRequest.setReason(dto.getReason());
            ChangeRequest.setStatus(ScheduleRequestStatus.PENDING);
            ChangeRequest.setRequestedDate(dto.getRequestedDate());
            ChangeRequest.setRequestedShift(dto.getRequestedShift());

            workScheduleRequestService.save(ChangeRequest);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to update schedule: " + e.getMessage());
        }
    }

}
