package dev.mochahaulier.bankingtest.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "INT_LOCK")
public class LockEntity {
    @Id
    private String lockKey;
    private String region;
    private String clientId;
    private Instant createdDate;
}