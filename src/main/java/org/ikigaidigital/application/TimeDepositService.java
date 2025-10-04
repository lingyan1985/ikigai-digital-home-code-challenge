package org.ikigaidigital.application;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.ikigaidigital.ports.TimeDepositRepository;

import java.util.List;

public class TimeDepositService {

    private final TimeDepositRepository repository;
    private final TimeDepositCalculator calculator;

    public TimeDepositService(TimeDepositRepository repository, TimeDepositCalculator calculator) {
        this.repository = repository;
        this.calculator = calculator;
    }

    // Use case: recalculate balances in-place and persist
    public List<TimeDeposit> recalculateAll() {
        List<TimeDeposit> all = repository.findAll();
        calculator.updateBalance(all); // preserve original behavior
        repository.saveAll(all);
        return all;
    }

    public List<TimeDeposit> getAll() {
        return repository.findAll();
    }
}
