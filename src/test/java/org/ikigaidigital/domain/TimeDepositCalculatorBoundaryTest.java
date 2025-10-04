package org.ikigaidigital.domain;

import org.ikigaidigital.domain.interest.InterestPolicyRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimeDepositCalculatorBoundaryTest {

    private final TimeDepositCalculator calc =
            new TimeDepositCalculator(InterestPolicyRegistry.defaultRegistry());

    @Test
    void day30_vs_day31_for_each_plan() {
        // day 30 -> no interest
        List<TimeDeposit> d30 = List.of(
                new TimeDeposit(1, "basic", 1000.00, 30),
                new TimeDeposit(2, "student", 1000.00, 30),
                new TimeDeposit(3, "premium", 1000.00, 30)
        );
        calc.updateBalance(d30);
        d30.forEach(td -> assertThat(td.getBalance()).isEqualTo(1000.00));

        // day 31
        List<TimeDeposit> d31 = List.of(
                new TimeDeposit(4, "basic", 1000.00, 31),   // 1000.83
                new TimeDeposit(5, "student", 1000.00, 31), // 1002.50
                new TimeDeposit(6, "premium", 1000.00, 31)  // still 1000.00
        );
        calc.updateBalance(d31);
        assertThat(d31.get(0).getBalance()).isEqualTo(1000.83);
        assertThat(d31.get(1).getBalance()).isEqualTo(1002.50);
        assertThat(d31.get(2).getBalance()).isEqualTo(1000.00);
    }

    @Test
    void premium_day45_vs_day46() {
        List<TimeDeposit> d45 = List.of(new TimeDeposit(1, "premium", 1000.00, 45));
        List<TimeDeposit> d46 = List.of(new TimeDeposit(2, "premium", 1000.00, 46));
        calc.updateBalance(d45);
        calc.updateBalance(d46);

        assertThat(d45.get(0).getBalance()).isEqualTo(1000.00);
        assertThat(d46.get(0).getBalance()).isEqualTo(1004.17); // 5%/12 => +4.17
    }

    @Test
    void student_day365_vs_day366() {
        List<TimeDeposit> d365 = List.of(new TimeDeposit(1, "student", 1000.00, 365));
        List<TimeDeposit> d366 = List.of(new TimeDeposit(2, "student", 1000.00, 366));
        calc.updateBalance(d365);
        calc.updateBalance(d366);

        assertThat(d365.get(0).getBalance()).isEqualTo(1002.50); // +2.50
        assertThat(d366.get(0).getBalance()).isEqualTo(1000.00); // +0.00
    }
}
