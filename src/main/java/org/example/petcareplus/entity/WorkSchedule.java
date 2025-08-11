package org.example.petcareplus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.petcareplus.enums.ScheduleStatus;
import org.example.petcareplus.enums.Shift;

import java.time.LocalDate;

@Entity
@Table(name = "WorkSchedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @NotNull
    @Column(name="work_date")
    private LocalDate workDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Shift shift;

    private String note;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
}
