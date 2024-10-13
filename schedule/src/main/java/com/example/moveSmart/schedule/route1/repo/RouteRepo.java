package com.example.moveSmart.schedule.route.repo;

import com.example.moveSmart.schedule.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RouteRepo extends JpaRepository<Route, Long> {
}
