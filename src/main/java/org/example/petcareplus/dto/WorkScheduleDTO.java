package org.example.petcareplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.example.petcareplus.enums.ScheduleStatus;
import org.example.petcareplus.enums.Shift;

import java.time.LocalDate;

public class WorkScheduleDTO {
    private String note;
    private String status;
    private Long accountId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    private String shift;

    // Constructors
    public WorkScheduleDTO() {
    }

    public WorkScheduleDTO(String note, String status, Long accountId, LocalDate workDate, String shift) {
        this.note = note;
        this.status = status;
        this.accountId = accountId;
        this.workDate = workDate;
        this.shift = shift;
    }

    // Getters and Setters
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ScheduleStatus getStatus() {
        return ScheduleStatus.valueOf(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public Shift getShift() {
        return Shift.valueOf(shift);
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}