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
            SELECT ab
            FROM AppointmentBooking ab
            JOIN Service s on ab.service.serviceId = s.serviceId
            JOIN PetProfile p on ab.petProfile.petProfileId = p.petProfileId
            JOIN Profile pr on p.profile.profileId = pr.profileId
            WHERE pr.profileId = :profileId
            """)
    List<AppointmentBooking> findByProfileId(Long profileId);

    @Query("""
            SELECT ab FROM AppointmentBooking ab
            JOIN PetProfile p on ab.petProfile.petProfileId = p.petProfileId
            JOIN Profile pr on p.profile.profileId= pr.profileId
            WHERE pr.profileId = :profileId and ab.status = "PENDING"
            """)
    List<AppointmentBooking> findByProfileIdAndStatus(Long profileId, BookingStatus status);

    Page<AppointmentBooking> findByStatus(BookingStatus status, Pageable pageable);
}
