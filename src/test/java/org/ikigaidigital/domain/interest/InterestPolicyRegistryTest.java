package org.ikigaidigital.domain.interest;

import org.ikigaidigital.domain.TimeDeposit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.offset;

class InterestPolicyRegistryTest {

    private final InterestPolicyRegistry registry = InterestPolicyRegistry.defaultRegistry();

    @Test
    void lookup_is_case_insensitive() {
        double i1 = registry.computeMonthlyInterest(new TimeDeposit(1, "BASIC", 1200.00, 60));
        double i2 = registry.computeMonthlyInterest(new TimeDeposit(2, "basic", 1200.00, 60));
        assertThat(i1).isEqualTo(i2); // both should be 1200 * 0.01 / 12 = 1.0
        assertThat(i1).isEqualTo(1.0);
    }

    @Test
    void fallback_for_unknown_plan_is_zero_interest() {
        double i = registry.computeMonthlyInterest(new TimeDeposit(1, "gold", 1000.00, 90));
        assertThat(i).isEqualTo(0.0);
    }

    @Test
    void null_plan_gracefully_returns_zero_interest() {
        double i = registry.computeMonthlyInterest(new TimeDeposit(1, null, 1000.00, 90));
        assertThat(i).isEqualTo(0.0);
    }

    @Test
    void respects_thresholds_in_policies() {
        // student day 366 -> zero
        double s = registry.computeMonthlyInterest(new TimeDeposit(1, "student", 2000.00, 366));
        assertThat(s).isEqualTo(0.0);

        // premium day 45 -> zero
        double p45 = registry.computeMonthlyInterest(new TimeDeposit(2, "premium", 5000.00, 45));
        assertThat(p45).isEqualTo(0.0);

        // premium day 46 -> 5000 * 0.05 / 12 = 20.8333333333...
        double p46 = registry.computeMonthlyInterest(new TimeDeposit(3, "premium", 5000.00, 46));

        // ✅ relaxed tolerance for floating-point arithmetic
        assertThat(p46).isCloseTo(20.8333, offset(1e-4));
    }
}
