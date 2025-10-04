package org.ikigaidigital.domain.interest;

import org.ikigaidigital.common.PlanType;
import org.ikigaidigital.domain.TimeDeposit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicStudentPremiumPolicyTest {

    private final BasicInterestPolicy basic = new BasicInterestPolicy();
    private final StudentInterestPolicy student = new StudentInterestPolicy();
    private final PremiumInterestPolicy premium = new PremiumInterestPolicy();

    @Test
    void basic_policy_rules() {
        assertThat(basic.supports(PlanType.BASIC)).isTrue();
        assertThat(basic.computeMonthlyInterest(new TimeDeposit(1, "basic", 1000.0, 30))).isEqualTo(0.0);
        assertThat(basic.computeMonthlyInterest(new TimeDeposit(2, "basic", 1000.0, 31)))
                .isEqualTo(1000.0 * 0.01 / 12.0);
    }

    @Test
    void student_policy_rules() {
        assertThat(student.supports(PlanType.STUDENT)).isTrue();
        assertThat(student.computeMonthlyInterest(new TimeDeposit(1, "student", 2000.0, 30))).isEqualTo(0.0);
        assertThat(student.computeMonthlyInterest(new TimeDeposit(2, "student", 2000.0, 120)))
                .isEqualTo(2000.0 * 0.03 / 12.0);
        assertThat(student.computeMonthlyInterest(new TimeDeposit(3, "student", 2000.0, 366))).isEqualTo(0.0);
    }

    @Test
    void premium_policy_rules() {
        assertThat(premium.supports(PlanType.PREMIUM)).isTrue();
        assertThat(premium.computeMonthlyInterest(new TimeDeposit(1, "premium", 5000.0, 45))).isEqualTo(0.0);
        assertThat(premium.computeMonthlyInterest(new TimeDeposit(2, "premium", 5000.0, 46)))
                .isEqualTo(5000.0 * 0.05 / 12.0);
    }
}
