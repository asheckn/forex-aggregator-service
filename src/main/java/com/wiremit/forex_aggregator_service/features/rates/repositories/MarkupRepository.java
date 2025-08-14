package com.wiremit.forex_aggregator_service.features.rates.repositories;

import com.wiremit.forex_aggregator_service.features.rates.entities.Markup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkupRepository extends JpaRepository<Markup, Long> {
}
