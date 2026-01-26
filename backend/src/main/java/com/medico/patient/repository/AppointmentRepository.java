package com.medico.patient.repository;

import com.medico.patient.domain.Appointment;
import com.medico.patient.domain.Appointment.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);

    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndDateRange(
        @Param("doctorId") UUID doctorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.appointmentDateTime >= :from")
    Page<Appointment> findByStatusAndDateAfter(
        @Param("status") AppointmentStatus status,
        @Param("from") LocalDateTime from,
        Pageable pageable
    );

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :start AND :end AND a.status NOT IN ('CANCELLED', 'NO_SHOW')")
    List<Appointment> findActiveAppointmentsByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(@Param("status") AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDateTime >= :start AND a.appointmentDateTime < :end")
    long countAppointmentsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    boolean existsByDoctorIdAndAppointmentDateTimeBetweenAndStatusNot(
        UUID doctorId,
        LocalDateTime start,
        LocalDateTime end,
        AppointmentStatus excludeStatus
    );
}

