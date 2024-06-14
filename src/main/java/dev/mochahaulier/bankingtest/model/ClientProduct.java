package dev.mochahaulier.bankingtest.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class ClientProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "product_key", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private LocalDate lastChargeDate;

    public ProductDefinition getProductDefinition() {
        return product.getProductDefinition();
    }

    public BigDecimal getRate() {
        return product.getRate();
    }

    public boolean isDueForCharge() {
        ProductDefinition productDefinition = product.getProductDefinition();
        if (lastChargeDate == null) {
            // Should probably set this at creation, but should be fine for now
            lastChargeDate = java.time.LocalDate.now(); // startDate;
        }
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastChargeDate, LocalDate.now());
        // assuming every month has 30 days
        // TODO: could use as follows:
        // long monthsBetween = ChronoUnit.MONTHS.between(lastChargeDate, currentDate);
        int intervalDays = productDefinition.getPayRateUnit() == PayRateUnit.DAY
                ? productDefinition.getPayRateValue()
                : productDefinition.getPayRateValue() * 30;

        return daysBetween >= intervalDays;
    }
}
