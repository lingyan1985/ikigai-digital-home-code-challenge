package org.ikigaidigital;

import org.ikigaidigital.application.TimeDepositService;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.infrastructure.persistence.TimeDepositRepositoryAdapter;
import org.ikigaidigital.infrastructure.persistence.jpa.TimeDepositJpaRepository;
import org.ikigaidigital.infrastructure.persistence.jpa.WithdrawalJpaRepository;
import org.ikigaidigital.ports.TimeDepositRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TimeDepositApp {

    public static void main(String[] args) {
        SpringApplication.run(TimeDepositApp.class, args);
    }

    @Bean
    TimeDepositRepository timeDepositRepository(TimeDepositJpaRepository td, WithdrawalJpaRepository w) {
        return new TimeDepositRepositoryAdapter(td, w);
    }

    @Bean
    TimeDepositService timeDepositService(TimeDepositRepository repo) {
        return new TimeDepositService(repo, new TimeDepositCalculator());
    }
}
