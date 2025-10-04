package org.ikigaidigital.domain.interest;

import org.ikigaidigital.domain.TimeDeposit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry mapping plan type -> policy. Keeps lookups O(1) and preserves a simple extension point.
 * Behavior is unchanged; only the decision logic moved out of TimeDepositCalculator.
 */
public class InterestPolicyRegistry {

    private final Map<String, InterestPolicy> byPlan;
    private final InterestPolicy fallback = new NoopInterestPolicy();

    public InterestPolicyRegistry(Map<String, InterestPolicy> byPlan) {
        this.byPlan = byPlan;
    }

    /** Create the registry with built-in policies matching original behavior. */
    public static InterestPolicyRegistry defaultRegistry() {
        Map<String, InterestPolicy> m = new LinkedHashMap<>();
        // Map lower-cased plan type to policy
        InterestPolicy basic = new BasicInterestPolicy();
        InterestPolicy student = new StudentInterestPolicy();
        InterestPolicy premium = new PremiumInterestPolicy();

        m.put("basic", basic);
        m.put("student", student);
        m.put("premium", premium);

        return new InterestPolicyRegistry(m);
    }

    public double computeMonthlyInterest(TimeDeposit td) {
        if (td == null || td.getPlanType() == null) return 0.0;
        InterestPolicy policy = byPlan.getOrDefault(td.getPlanType().toLowerCase(), fallback);
        return policy.computeMonthlyInterest(td);
    }
}
