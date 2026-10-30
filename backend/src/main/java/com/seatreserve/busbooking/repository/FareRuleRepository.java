package com.seatreserve.busbooking.repository;

import com.seatreserve.busbooking.model.entity.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FareRuleRepository extends JpaRepository<FareRule, Long> {
    Optional<FareRule> findByRuleName(String ruleName);
}
