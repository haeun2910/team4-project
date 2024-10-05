package com.example.schedule.schedule.repo;

import com.example.schedule.schedule.entity.TransportationOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportationOptRepo extends JpaRepository<TransportationOption, Long> {
}
