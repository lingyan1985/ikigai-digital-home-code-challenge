package org.ikigaidigital.domain;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class Withdrawal {
    // Getters
    private final int id;
    private final int timeDepositId;
    private final BigDecimal amount;
    private final LocalDate date;

    public Withdrawal(int id, int timeDepositId, BigDecimal amount, LocalDate date) {
        this.id = id;
        this.timeDepositId = timeDepositId;
        this.amount = amount;
        this.date = date;
    }
}