package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;

/** Fallback for unknown plan types: accrues no interest. */
public class NoopInterestPolicy implements InterestPolicy {
    @Override
    public double computeMonthlyInterest(TimeDeposit td) {
        return 0.0;
    }
    @Override
    public boolean supports(PlanType planType) {
        return false;
    }
}
