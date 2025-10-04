package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;

/**
 * Computes the *raw* monthly interest (unrounded).
 * Rounding and balance mutation remain in TimeDepositCalculator (to preserve behavior).
 */
public interface InterestPolicy {
    /**
     * @param td the time deposit
     * @return raw monthly interest as double (unrounded).
     */
    double computeMonthlyInterest(TimeDeposit td);

    /**
     * @param planType plan type string (e.g., "basic")
     * @return true if this policy handles the given plan (case-insensitive)
     */
    boolean supports(PlanType planType);
}
