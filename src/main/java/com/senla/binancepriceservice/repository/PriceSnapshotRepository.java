package com.senla.binancepriceservice.repository;

import com.senla.binancepriceservice.entity.PriceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {}
