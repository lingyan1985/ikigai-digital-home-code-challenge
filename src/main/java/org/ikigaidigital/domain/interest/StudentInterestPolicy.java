package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;

import static org.ikigaidigital.common.PlanType.STUDENT;

/**
 * Student plan: 3% annual, monthly accrual after day > 30, but NO interest once days >= 366.
 */
public class StudentInterestPolicy implements InterestPolicy {

    private static final double ANNUAL = 0.03;
    private static final double MONTHS = 12.0;

    @Override
    public double computeMonthlyInterest(TimeDeposit td) {
        if (td.getDays() <= 30) return 0.0;   // global rule
        if (td.getDays() >= 366) return 0.0;  // cutoff after 1 year (original: < 366)

        return td.getBalance() * ANNUAL / MONTHS;
    }

    @Override
    public boolean supports(PlanType planType) {
        return STUDENT == planType;
    }
}
