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
}

