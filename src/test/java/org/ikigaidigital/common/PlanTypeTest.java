package org.ikigaidigital.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanTypeTest {

    @Test
    void from_parses_values_and_handles_unknowns() {
        assertThat(PlanType.from("basic")).isEqualTo(PlanType.BASIC);
        assertThat(PlanType.from("BASIC")).isEqualTo(PlanType.BASIC);
        assertThat(PlanType.from("Student")).isEqualTo(PlanType.STUDENT);
        assertThat(PlanType.from("PREMIUM")).isEqualTo(PlanType.PREMIUM);
        assertThat(PlanType.from("gold")).isEqualTo(PlanType.UNKNOWN);
        assertThat(PlanType.from(null)).isEqualTo(PlanType.UNKNOWN);
        assertThat(PlanType.from("   premium   ")).isEqualTo(PlanType.PREMIUM);
    }
}
