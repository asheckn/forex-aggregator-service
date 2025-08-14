package com.wiremit.forex_aggregator_service.features.rates.repositories;

import com.wiremit.forex_aggregator_service.features.rates.entities.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

   Optional<Currency> findByCode(String code);
}
