package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;

import static org.ikigaidigital.common.PlanType.BASIC;

/**
 * Basic plan: 1% annual, monthly accrual after day > 30.
 */
public class BasicInterestPolicy implements InterestPolicy {

    private static final double ANNUAL = 0.01;
    private static final double MONTHS = 12.0;

    @Override
    public double computeMonthlyInterest(TimeDeposit td) {
        // Global rule: no interest for the first 30 days (same as original)
        if (td.getDays() <= 30) return 0.0;

        return td.getBalance() * ANNUAL / MONTHS;
    }

    @Override
    public boolean supports(PlanType planType) {
        return BASIC == planType;
    }
}
