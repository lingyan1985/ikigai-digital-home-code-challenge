package org.ikigaidigital.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalJpaRepository extends JpaRepository<WithdrawalEntity, Integer> {
    List<WithdrawalEntity> findByTimeDeposit_Id(Integer id);
}
