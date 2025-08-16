package org.example.petcareplus.dto;

import org.example.petcareplus.enums.Shift;

import java.time.LocalDate;

public class ChangeRequestDTO {
    private String reason;
    private Long scheduleId;
    private LocalDate requestedDate;
    private Shift requestedShift;

    public ChangeRequestDTO(String reason, Long scheduleId, LocalDate requestedDate, Shift requestedShift) {
        this.reason = reason;
        this.scheduleId = scheduleId;
        this.requestedDate = requestedDate;
        this.requestedShift = requestedShift;
    }

    public ChangeRequestDTO() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Shift getRequestedShift() {
        return requestedShift;
    }

    public void setRequestedShift(Shift requestedShift) {
        this.requestedShift = requestedShift;
    }
}