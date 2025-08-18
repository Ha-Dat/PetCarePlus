package org.example.petcareplus.repository;

import org.example.petcareplus.dto.MyServiceDTO;
import org.example.petcareplus.entity.AppointmentBooking;
import org.example.petcareplus.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentBooking, Long> {
    Page<AppointmentBooking> findAll(Pageable pageable);

    Page<AppointmentBooking> findByStatusIgnoreCase(String status, Pageable pageable);

    @Query("""
        SELECT DISTINCT new org.example.petcareplus.dto.MyServiceDTO(
            ab.appointmentBookingId,
            p.name,
            s.name,
            s.serviceCategory,
            ab.bookDate,
            ab.status,
            ab.note
        )
        FROM AppointmentBooking ab
        JOIN ab.service s
        JOIN ab.petProfile p
        JOIN p.profile pr
        WHERE pr.profileId = :profileId
    """)
    List<MyServiceDTO> findByProfileId(Long profileId);

    @Query("""
        SELECT new org.example.petcareplus.dto.MyServiceDTO(
            ab.appointmentBookingId,
            p.name,
            s.name,
            s.serviceCategory,
            ab.bookDate,
            ab.status,
            ab.note
        )
        FROM AppointmentBooking ab
        JOIN ab.petProfile p
        JOIN p.profile pr
        JOIN ab.service s
        WHERE pr.profileId = :profileId
        AND ab.status = :status
    """)
    List<MyServiceDTO> findByProfileIdAndStatus(Long profileId, BookingStatus status);

    Page<AppointmentBooking> findByStatus(BookingStatus status, Pageable pageable);

    // Kiểm tra xem có booking nào đang sử dụng service không
    boolean existsByServiceServiceId(Long serviceId);

    // Tìm tất cả booking theo petProfileId
    List<AppointmentBooking> findByPetProfile_petProfileId(Long petProfileId);
}
