package com.zerobase.reservationapi.repository;


import com.zerobase.reservationapi.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    Optional<Reservation> findByIdAndCustomerId(Long id, Long memberId);

    Page<Reservation> findByCustomerId(Long memberId, Pageable pageable);

    Page<Reservation> findByCustomerIdAndStoreId(Long memberId, Long storeId, Pageable pageable);

    Page<Reservation> findByStoreIdAndReservationDate(Long storeId, LocalDate date, Pageable pageable);

    Optional<Reservation> findByCustomerIdAndStoreReservationInfoIdAndReservationDate(Long id, Long storeReservationInfoId, LocalDate reservationDate);

}