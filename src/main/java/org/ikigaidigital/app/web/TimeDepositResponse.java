package org.ikigaidigital.app.web;

import java.time.LocalDate;
import java.util.List;

public record TimeDepositResponse(
        int id,
        String planType,
        double balance,
        int days,
        List<WithdrawalResponse> withdrawals
) {
    public record WithdrawalResponse(int id, double amount, LocalDate date) {
    }
}
