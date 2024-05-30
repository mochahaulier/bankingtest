package dev.mochahaulier.bankingtest.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRate {
    @Enumerated(EnumType.STRING)
    private PayRateUnit unit;

    private int value;
}
