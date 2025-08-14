package com.wiremit.forex_aggregator_service.features.rates.repositories;

import com.wiremit.forex_aggregator_service.features.rates.entities.Currency;
import com.wiremit.forex_aggregator_service.features.rates.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByDate(LocalDate date);
    List<Rate> findByDateAndBaseCurrency(LocalDate date, Currency baseCurrency);

    /**
     * Find by Date between and Base Currency
     */

    List<Rate> findByDateBetweenAndBaseCurrency(LocalDate startDate, LocalDate endDate, Currency baseCurrency);

    /**
     * Finds the most recent rate for a specific currency pair
     */
    Optional<Rate> findTopByBaseCurrencyAndTargetCurrencyOrderByDateDesc(
            Currency baseCurrency, Currency targetCurrency);

}
