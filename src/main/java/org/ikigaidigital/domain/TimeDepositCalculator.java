package org.ikigaidigital.domain;

import org.ikigaidigital.domain.interest.InterestPolicyRegistry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Refactored to delegate interest computation to InterestPolicyRegistry,
 * while preserving original behavior (rounding and signature unchanged).
 */
public class TimeDepositCalculator {
    private final InterestPolicyRegistry registry;

    public TimeDepositCalculator() {
        this.registry = InterestPolicyRegistry.defaultRegistry();
    }

    // Useful for tests/DI
    public TimeDepositCalculator(InterestPolicyRegistry registry) {
        this.registry = registry;
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit td : xs) {
            // Compute *raw* monthly interest (unrounded), identical to old logic.
            double interest = registry.computeMonthlyInterest(td);

            // Original rounding behavior preserved: round interest to 2dp, then add.
            double roundedInterest = new BigDecimal(interest)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            double newBalance = td.getBalance() + roundedInterest;
            td.setBalance(newBalance);
        }
    }
}
