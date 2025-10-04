package org.ikigaidigital.infrastructure;

import org.ikigaidigital.TimeDepositApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TimeDepositApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TimeDepositApiIT {

    static final PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("xa")
            .withUsername("xa")
            .withPassword("xa");

    static {
        pg.start();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
        r.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    TestRestTemplate rest;

    @BeforeEach
    void seed() {
        // Clean and seed deterministic data
        jdbc.update("delete from withdrawals");
        jdbc.update("delete from time_deposits");

        // basic: >30 days -> should accrue 0.83 on 1000.00
        jdbc.update("insert into time_deposits(id, plan_type, days, balance) values (?,?,?,?)",
                1, "basic", 60, 1000.00);

        // student: 120 days -> 3%/12 * 2000 = 5.00
        jdbc.update("insert into time_deposits(id, plan_type, days, balance) values (?,?,?,?)",
                2, "student", 120, 2000.00);

        // premium: 50 days (>45) -> 5%/12 * 5000 = 20.83
        jdbc.update("insert into time_deposits(id, plan_type, days, balance) values (?,?,?,?)",
                3, "premium", 50, 5000.00);

        // student: 366 days -> no interest
        jdbc.update("insert into time_deposits(id, plan_type, days, balance) values (?,?,?,?)",
                4, "student", 366, 3000.00);

        // withdrawals sample
        jdbc.update("insert into withdrawals(id, time_deposit_id, amount, date) values (?,?,?, CURRENT_DATE)",
                10, 1, 50.00);
    }

    @Test
    void get_returns_all_with_withdrawals_schema() {
        ResponseEntity<List<Map<String, Object>>> resp =
                rest.exchange("/api/v1/time-deposits",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {});

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> list = resp.getBody();
        assertThat(list).isNotNull().isNotEmpty();

// 🔑 Pick the deposit we know has a withdrawal (id=1 in our seed)
        Map<String, Object> td1 = list.stream()
                .filter(m -> ((Number) m.get("id")).intValue() == 1)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "Could not find time deposit with id=1. Got ids: " + list.stream().map(m -> m.get("id")).toList()
                ));

        // Assert schema keys (camelCase)
        assertThat(td1).containsKeys("id", "planType", "balance", "days", "withdrawals");

        // withdrawals present and non-empty for id=1
        Object wObj = td1.get("withdrawals");
        assertThat(wObj).isInstanceOf(List.class);
        List<?> withdrawals = (List<?>) wObj;
        assertThat(withdrawals).isNotEmpty();

        // Assert withdrawal item keys
        Object firstW = withdrawals.get(0);
        assertThat(firstW).isInstanceOf(Map.class);
        Map<?, ?> w = (Map<?, ?>) firstW;
        assertThat((Map<String, Object>) w).containsKeys("id", "amount", "date");


    }

    @Test
    void recalculate_updates_balances_and_persists() {
        // POST recalc
        ResponseEntity<List> postResp = rest.postForEntity("/api/v1/time-deposits/recalculate", null, List.class);
        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // GET and verify balances updated
        ResponseEntity<List> getResp = rest.getForEntity("/api/v1/time-deposits", List.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        List body = getResp.getBody();
        assertThat(body).isNotEmpty();

        // Verify expected balances after one monthly accrual
        double basicBalance = findBalance(body, 1);
        double studentBalance = findBalance(body, 2);
        double premiumBalance = findBalance(body, 3);
        double studentNoInterestBalance = findBalance(body, 4);

        assertThat(basicBalance).isEqualTo(1000.83);          // +0.83
        assertThat(studentBalance).isEqualTo(2005.00);        // +5.00
        assertThat(premiumBalance).isEqualTo(5020.83);        // +20.83
        assertThat(studentNoInterestBalance).isEqualTo(3000.00); // unchanged
    }

    private double findBalance(List<Map<String,Object>> items, int id) {
        Map<String,Object> item = items.stream()
                .filter(m -> ((Number)m.get("id")).intValue() == id)
                .findFirst().orElseThrow();
        return ((Number)item.get("balance")).doubleValue();
    }
}
