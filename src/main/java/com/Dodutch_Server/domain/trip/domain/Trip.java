package com.Dodutch_Server.domain.trip.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer budget;
    private Integer totalCost;
    private String password;

    @OneToMany(mappedBy = "trip")
    private List<TripMember> tripMembers = new ArrayList<>();
}
