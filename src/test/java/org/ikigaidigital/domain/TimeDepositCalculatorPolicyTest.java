package org.ikigaidigital.domain;

import org.ikigaidigital.domain.interest.InterestPolicyRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeDepositCalculatorPolicyTest {

    private final TimeDepositCalculator calc =
            new TimeDepositCalculator(InterestPolicyRegistry.defaultRegistry());

    @Test
    void noInterestForFirst30Days_allPlans() {
        List<TimeDeposit> xs = List.of(
                new TimeDeposit(1, "basic",   1000.00, 30),
                new TimeDeposit(2, "student", 1000.00, 30),
                new TimeDeposit(3, "premium", 1000.00, 30)
        );
        calc.updateBalance(xs);
        assertThat(xs).extracting(TimeDeposit::getBalance)
                .containsExactly(1000.00, 1000.00, 1000.00);
    }

    @Test
    void basic_after30Days_accrues_1pct_annual_monthly() {
        List<TimeDeposit> xs = List.of(new TimeDeposit(1, "basic", 1000.00, 31));
        calc.updateBalance(xs);
        // 1% annual -> /12 on 1000 = 0.8333 -> rounds 0.83 -> 1000.83
        assertThat(xs.get(0).getBalance()).isEqualTo(1000.83);
    }

    @Test
    void premium_requires_more_than_45_days() {
        List<TimeDeposit> d45 = List.of(new TimeDeposit(1, "premium", 5000.00, 45));
        List<TimeDeposit> d46 = List.of(new TimeDeposit(2, "premium", 5000.00, 46));

        calc.updateBalance(d45);
        calc.updateBalance(d46);

        assertThat(d45.get(0).getBalance()).isEqualTo(5000.00); // no interest at 45
        // 5% annual -> /12 on 5000 = 20.8333 -> 20.83 -> 5020.83
        assertThat(d46.get(0).getBalance()).isEqualTo(5020.83);
    }

    @Test
    void student_accrues_until_day_365_but_not_from_366() {
        List<TimeDeposit> d365 = List.of(new TimeDeposit(1, "student", 2000.00, 365));
        List<TimeDeposit> d366 = List.of(new TimeDeposit(2, "student", 2000.00, 366));

        calc.updateBalance(d365);
        calc.updateBalance(d366);

        // 3% annual -> /12 on 2000 = 5.00 -> 2005.00
        assertThat(d365.get(0).getBalance()).isEqualTo(2005.00);
        assertThat(d366.get(0).getBalance()).isEqualTo(2000.00);
    }

    @Test
    void unknownPlan_gets_no_interest_even_after_thresholds() {
        List<TimeDeposit> xs = List.of(new TimeDeposit(1, "vip-plus", 777.77, 180));
        calc.updateBalance(xs);
        assertThat(xs.get(0).getBalance()).isEqualTo(777.77);
    }
}
