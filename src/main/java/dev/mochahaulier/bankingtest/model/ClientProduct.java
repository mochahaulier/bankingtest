package dev.mochahaulier.bankingtest.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
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

    // For accounts
    private BigDecimal balance;

    // For loans
    private BigDecimal originalAmount;
    private BigDecimal fixedInstallment;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastChargeDate;

    public ProductDefinition getProductDefinition() {
        return product.getProductDefinition();
    }

    public BigDecimal getRate() {
        return product.getRate();
    }

    // Return the number of due dates since
    // Might be good to save in the DB directly, but then would need to update with
    // changes in PayRate.
    public int calculateNumberOfDueDates() {
        // shouldn't happen
        if (endDate == null || startDate == null)
            return 0;

        ProductDefinition productDefinition = product.getProductDefinition();
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);

        // Assuming that each month has 30 days...
        int intervalDays = productDefinition.getPayRateUnit() == PayRateUnit.DAY ? productDefinition.getPayRateValue()
                : productDefinition.getPayRateValue() * 30;

        return (int) (daysBetween / intervalDays);
    }

    public boolean isDueForCharge() {
        ProductDefinition productDefinition = product.getProductDefinition();
        if (lastChargeDate == null) {
            // Should probably set this at creation, but should be fine for now
            lastChargeDate = startDate;
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
