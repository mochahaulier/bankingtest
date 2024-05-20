package dev.mochahaulier.bankingtest.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class PayRate {
    @Enumerated(EnumType.STRING)
    private PayRateUnit unit;
    private int value;
}
