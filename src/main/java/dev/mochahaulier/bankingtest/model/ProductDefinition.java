package dev.mochahaulier.bankingtest.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProductDefinition {
    @Id
    private String productKey;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private BigDecimal rate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unit", column = @Column(name = "pay_rate_unit")),
            @AttributeOverride(name = "value", column = @Column(name = "pay_rate_value"))
    })
    private PayRate payRate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdDate;
    @UpdateTimestamp
    private Instant modifiedDate;

    public PayRateUnit getPayRateUnit() {
        return this.payRate.getUnit();
    }

    public int getPayRateValue() {
        return this.payRate.getValue();
    }

    public RateType getRateType() {
        // If rate bigger than 1 set as fixed, otherwise use percentage, big assumption
        return this.rate.compareTo(BigDecimal.ONE) == 1 ? RateType.FIXED : RateType.PERCENTAGE;
    }
}
