package org.example.petcareplus.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petcareplus.enums.RequestType;
import org.example.petcareplus.enums.ScheduleRequestStatus;
import org.example.petcareplus.enums.ScheduleStatus;
import org.example.petcareplus.enums.Shift;
import java.time.LocalDate;

@Entity
@Table(name = "WorkScheduleRequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkScheduleRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id")
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private WorkSchedule originalSchedule;

    @Enumerated(EnumType.STRING)
    @Column(name="request_type", nullable = false)
    private RequestType requestType;

    @NotNull
    @Column(name="requested_date")
    private LocalDate requestedDate;

    @NotNull
    @Column(name="requested_shift")
    @Enumerated(EnumType.STRING)
    private Shift requestedShift;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleRequestStatus status;

    public WorkScheduleRequest() {
    }

    public WorkScheduleRequest(Long requestId, Account account, WorkSchedule originalSchedule, RequestType requestType, LocalDate requestedDate, Shift requestedShift, String reason, ScheduleRequestStatus status) {
        this.requestId = requestId;
        this.account = account;
        this.originalSchedule = originalSchedule;
        this.requestType = requestType;
        this.requestedDate = requestedDate;
        this.requestedShift = requestedShift;
        this.reason = reason;
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public WorkSchedule getOriginalSchedule() {
        return originalSchedule;
    }

    public void setOriginalSchedule(WorkSchedule originalSchedule) {
        this.originalSchedule = originalSchedule;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ScheduleRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ScheduleRequestStatus status) {
        this.status = status;
    }
}

