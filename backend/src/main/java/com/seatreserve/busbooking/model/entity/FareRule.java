package com.seatreserve.busbooking.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fare_rules")
public class FareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleName = "DEFAULT_RULE";

    @Column(nullable = false)
    private Double baseRatePerKm = 1.85;

    @Column(nullable = false)
    private Double sleeperSurchargePercent = 20.0;

    @Column(nullable = false)
    private Double windowSurchargePercent = 5.0;

    @Column(nullable = false)
    private Double taxRatePercent = 5.0;

    // Cancellation Policy Tiers (hours before departure vs cancellation fee %)
    @Column(nullable = false)
    private Integer tier1Hours = 24; // > 24 hours
    @Column(nullable = false)
    private Double tier1FeePercent = 10.0; // 10% fee, 90% refund

    @Column(nullable = false)
    private Integer tier2Hours = 12; // 12 - 24 hours
    @Column(nullable = false)
    private Double tier2FeePercent = 25.0; // 25% fee, 75% refund

    @Column(nullable = false)
    private Integer tier3Hours = 4; // 4 - 12 hours
    @Column(nullable = false)
    private Double tier3FeePercent = 50.0; // 50% fee, 50% refund

    @Column(nullable = false)
    private Double tier4FeePercent = 100.0; // < 4 hours, 0% refund

    public FareRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Double getBaseRatePerKm() { return baseRatePerKm; }
    public void setBaseRatePerKm(Double baseRatePerKm) { this.baseRatePerKm = baseRatePerKm; }

    public Double getSleeperSurchargePercent() { return sleeperSurchargePercent; }
    public void setSleeperSurchargePercent(Double sleeperSurchargePercent) { this.sleeperSurchargePercent = sleeperSurchargePercent; }

    public Double getWindowSurchargePercent() { return windowSurchargePercent; }
    public void setWindowSurchargePercent(Double windowSurchargePercent) { this.windowSurchargePercent = windowSurchargePercent; }

    public Double getTaxRatePercent() { return taxRatePercent; }
    public void setTaxRatePercent(Double taxRatePercent) { this.taxRatePercent = taxRatePercent; }

    public Integer getTier1Hours() { return tier1Hours; }
    public void setTier1Hours(Integer tier1Hours) { this.tier1Hours = tier1Hours; }

    public Double getTier1FeePercent() { return tier1FeePercent; }
    public void setTier1FeePercent(Double tier1FeePercent) { this.tier1FeePercent = tier1FeePercent; }

    public Integer getTier2Hours() { return tier2Hours; }
    public void setTier2Hours(Integer tier2Hours) { this.tier2Hours = tier2Hours; }

    public Double getTier2FeePercent() { return tier2FeePercent; }
    public void setTier2FeePercent(Double tier2FeePercent) { this.tier2FeePercent = tier2FeePercent; }

    public Integer getTier3Hours() { return tier3Hours; }
    public void setTier3Hours(Integer tier3Hours) { this.tier3Hours = tier3Hours; }

    public Double getTier3FeePercent() { return tier3FeePercent; }
    public void setTier3FeePercent(Double tier3FeePercent) { this.tier3FeePercent = tier3FeePercent; }

    public Double getTier4FeePercent() { return tier4FeePercent; }
    public void setTier4FeePercent(Double tier4FeePercent) { this.tier4FeePercent = tier4FeePercent; }
}
