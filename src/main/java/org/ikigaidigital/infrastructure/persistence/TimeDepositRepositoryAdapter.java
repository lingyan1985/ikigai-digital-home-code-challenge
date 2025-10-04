package org.ikigaidigital.infrastructure.persistence;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.Withdrawal;
import org.ikigaidigital.infrastructure.persistence.jpa.*;
import org.ikigaidigital.ports.TimeDepositRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TimeDepositRepositoryAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository tdRepo;
    private final WithdrawalJpaRepository wRepo;

    public TimeDepositRepositoryAdapter(TimeDepositJpaRepository tdRepo, WithdrawalJpaRepository wRepo) {
        this.tdRepo = tdRepo; this.wRepo = wRepo;
    }

    @Override
    public List<TimeDeposit> findAll() {
        return tdRepo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void saveAll(List<TimeDeposit> deposits) {
        List<TimeDepositEntity> entities = deposits.stream().map(this::toEntity).toList();
        tdRepo.saveAll(entities);
    }

    @Override
    public List<Withdrawal> findWithdrawalsFor(int timeDepositId) {
        return wRepo.findByTimeDeposit_Id(timeDepositId).stream()
                .map(this::toDomain)
                .toList();
    }

    private TimeDeposit toDomain(TimeDepositEntity e) {
        return new TimeDeposit(e.getId(), e.getPlanType(), e.getBalance().doubleValue(), e.getDays());
    }

    private TimeDepositEntity toEntity(TimeDeposit d) {
        TimeDepositEntity e = new TimeDepositEntity();
        e.setId(d.getId());
        e.setPlanType(d.getPlanType());
        e.setDays(d.getDays());
        BigDecimal scaled = BigDecimal
                .valueOf(d.getBalance())
                .setScale(2, RoundingMode.HALF_UP);

        e.setBalance(scaled);
        return e;
    }

    private Withdrawal toDomain(WithdrawalEntity w) {
        return new Withdrawal(
                w.getId(),
                w.getTimeDeposit().getId(),
                w.getAmount(),              // BigDecimal from JPA
                w.getDate()
        );
    }
}
