package com.wiremit.forex_aggregator_service.features.rates.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Currency baseCurrency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_currency_id", nullable = false)
    private Currency targetCurrency;

    @Column(nullable = false)
    private Double rate; // final rate after markup

    @Column(nullable = false)
    private Double originalRate; //  rate before markup

    @Column(nullable = false)
    private Double markup; //  markup percentage used to calculate

    @Column(nullable = false)
    private LocalDate date;
}

