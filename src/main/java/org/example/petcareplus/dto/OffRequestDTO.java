package org.example.petcareplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.petcareplus.enums.ScheduleStatus;
import org.example.petcareplus.enums.Shift;

import java.time.LocalDate;

public class OffRequestDTO {
    private String reason;
    private Long scheduleId;

    public OffRequestDTO(String reason, Long scheduleId) {
        this.reason = reason;
        this.scheduleId = scheduleId;
    }

    public OffRequestDTO() {
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
}