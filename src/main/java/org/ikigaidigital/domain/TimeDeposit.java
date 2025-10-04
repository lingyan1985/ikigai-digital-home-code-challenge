package org.ikigaidigital.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TimeDeposit {
    private final int id;
    private final String planType;
    @Setter
    private Double balance;
    private final int days;

    public TimeDeposit(int id, String planType, Double balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

}
