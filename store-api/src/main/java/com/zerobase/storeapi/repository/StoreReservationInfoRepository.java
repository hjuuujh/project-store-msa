package com.zerobase.storeapi.repository;

import com.zerobase.storeapi.domain.store.entity.StoreReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreReservationInfoRepository extends JpaRepository<StoreReservationInfo, Long> {
    List<StoreReservationInfo> findByStoreId(Long storeId);

    void deleteAllByIdIn(List<Long> ids);

    Optional<StoreReservationInfo> findByIdAndPartnerId(Long id, Long productId);
}