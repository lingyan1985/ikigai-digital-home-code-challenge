package org.ikigaidigital.ports;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;

import java.util.List;

public interface TimeDepositRepository {
    List<TimeDeposit> findAll();

    void saveAll(List<TimeDeposit> deposits);

    List<Withdrawal> findWithdrawalsFor(int timeDepositId);

}
