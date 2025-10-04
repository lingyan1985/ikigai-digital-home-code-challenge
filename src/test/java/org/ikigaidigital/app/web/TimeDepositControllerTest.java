package org.ikigaidigital.app.web;

import org.ikigaidigital.application.TimeDepositService;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.ports.TimeDepositRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeDepositController.class)
class TimeDepositControllerTest {
    @Resource
    MockMvc mvc;

    @MockBean
    TimeDepositService service;

    @MockBean
    TimeDepositRepository repo;

    @Test
    void getAll_schema_ok() throws Exception {
        Mockito.when(service.getAll()).thenReturn(List.of(
                new TimeDeposit(1, "basic", 1000.00, 60)
        ));
        Mockito.when(repo.findWithdrawalsFor(1)).thenReturn(List.of(
                new org.ikigaidigital.domain.Withdrawal(10, 1, new BigDecimal("50.00"), LocalDate.of(2025, 10, 4))
        ));

        mvc.perform(get("/api/v1/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].planType", is("basic")))
                .andExpect(jsonPath("$[0].balance", is(1000.0)))
                .andExpect(jsonPath("$[0].days", is(60)))
                .andExpect(jsonPath("$[0].withdrawals[0].id", is(10)))
                .andExpect(jsonPath("$[0].withdrawals[0].amount", is(50.0)))
                .andExpect(jsonPath("$[0].withdrawals[0].date", notNullValue()));
    }
}