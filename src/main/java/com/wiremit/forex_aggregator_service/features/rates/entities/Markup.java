package com.wiremit.forex_aggregator_service.features.rates.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
