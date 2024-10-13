package com.example.moveSmart.schedule.route.entity;

import com.example.moveSmart.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Location extends BaseEntity {
    private String locationName;
    private String address;
    // vĩ độ
    private double latitude;
    // kinh độ
    private double longitude;

}
