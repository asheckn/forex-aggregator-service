package com.wiremit.forex_aggregator_service.features.rates.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "markups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Markup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double percentage; // e.g., 10.0 means 10%
}
