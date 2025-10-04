package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;

import static org.ikigaidigital.common.PlanType.PREMIUM;

/**
 * Premium plan: 5% annual, monthly accrual only when days > 45 (and of course > 30 global rule).
 * In original code: requires days > 30 due to outer check AND > 45 here.
 * We model both checks explicitly to preserve behavior.
 */
public class PremiumInterestPolicy implements InterestPolicy {

    private static final double ANNUAL = 0.05;
    private static final double MONTHS = 12.0;

    @Override
    public double computeMonthlyInterest(TimeDeposit td) {
        if (td.getDays() <= 30) return 0.0; // global rule
        if (td.getDays() <= 45) return 0.0; // premium-specific threshold

        return td.getBalance() * ANNUAL / MONTHS;
    }

    @Override
    public boolean supports(PlanType planType) {
        return PREMIUM == planType;
    }
}
